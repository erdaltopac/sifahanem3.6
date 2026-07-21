package com.hazerfen.sifahane.alarm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.KeyguardManager
import android.os.PowerManager
import android.os.Build
import com.hazerfen.sifahane.AlarmActivity
import com.hazerfen.sifahane.R

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Alarm audio and vibration are owned by AlarmActivity. A silent channel avoids
        // overlapping notification-channel audio with the full-screen alarm audio.
        val channelId = "medicine_group_alarm_silent_v3353"
        val manager = context.getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.createNotificationChannel(
                NotificationChannel(channelId, "İlaç Alarmları", NotificationManager.IMPORTANCE_HIGH).apply {
                    description = "Aynı saatte alınacak ilaçlar için grup alarmı"
                    enableVibration(false)
                    setSound(null, null)
                    lockscreenVisibility = Notification.VISIBILITY_PRIVATE
                    setBypassDnd(true)
                }
            )
        }

        val ids = intent.getLongArrayExtra("medicationIds") ?: return
        val planned = intent.getLongArrayExtra("plannedDateTimes") ?: return
        val profiles = intent.getLongArrayExtra("profileIds") ?: LongArray(ids.size) { 1L }
        if (ids.size != planned.size || ids.size != profiles.size || ids.isEmpty()) return
        val groupKey = intent.getStringExtra("groupKey") ?: "GROUP:${System.currentTimeMillis()}"
        val deliveryStore = context.getSharedPreferences("alarm_delivery_v3353", Context.MODE_PRIVATE)
        if (deliveryStore.getBoolean("handled:$groupKey", false)) return
        val deliveredAt = deliveryStore.getLong(groupKey, 0L)
        if (System.currentTimeMillis() - deliveredAt < 30_000L) return
        deliveryStore.edit().putLong(groupKey, System.currentTimeMillis()).apply()
        context.getSharedPreferences("sifahane_alarm_diagnostics", Context.MODE_PRIVATE).edit()
            .putLong("last_triggered_at", System.currentTimeMillis())
            .putString("last_group_key", groupKey)
            .putInt("last_dose_count", ids.size)
            .apply()
        val isTest = intent.getBooleanExtra("isTest", false)
        val kind = intent.getStringExtra("alarmKind") ?: MedicationAlarmKind.NORMAL.name
        val names = intent.getStringArrayListExtra("medicationNames").orEmpty()

        val alarmIntent = Intent(context, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtras(intent)
        }
        val notificationId = AlarmScheduler.notificationId(context, groupKey)
        val fullScreenPending = PendingIntent.getActivity(
            context,
            notificationId,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val title = when {
            isTest -> "ŞİFAHANE DENEME ALARMI"
            kind == MedicationAlarmKind.CATCH_UP.name -> "GEÇMİŞ İLAÇ SAATİ"
            else -> "İLAÇ SAATİ"
        }
        val detail = when (ids.size) {
            1 -> "${names.firstOrNull() ?: "İlaç"} için işlem yapmanız gerekiyor"
            else -> "Birlikte alınacak ${ids.size} ilaç için işlem yapmanız gerekiyor"
        }
        val notification = Notification.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_sifahane)
            .setContentTitle(title).setContentText(detail)
            .setCategory(Notification.CATEGORY_ALARM).setPriority(Notification.PRIORITY_MAX)
            .setVisibility(Notification.VISIBILITY_PRIVATE).setOngoing(true).setAutoCancel(false)
            .setPublicVersion(
                Notification.Builder(context, channelId)
                    .setSmallIcon(R.drawable.ic_sifahane)
                    .setContentTitle("Şifahane alarmı")
                    .setContentText("Uygulamayı açarak alarmı yönetin")
                    .setCategory(Notification.CATEGORY_ALARM)
                    .build()
            )
            .setFullScreenIntent(fullScreenPending, true).setContentIntent(fullScreenPending)
            .setTimeoutAfter(30 * 60 * 1000L).build()
        manager.notify(notificationId, notification)
        // Kilitli durumda NotificationManager tek full-screen akışını yönetir. Kilit açık
        // ve ekran etkinse Activity doğrudan yalnız bir kez başlatılır; çift onNewIntent yoktur.
        val powerManager = context.getSystemService(PowerManager::class.java)
        val keyguardManager = context.getSystemService(KeyguardManager::class.java)
        if (powerManager.isInteractive && !keyguardManager.isKeyguardLocked) {
            runCatching { context.startActivity(alarmIntent) }
        }

        if (!isTest && kind == MedicationAlarmKind.NORMAL.name) {
            val doses = ids.indices.map { index ->
                AlarmDose(
                    ids[index],
                    profiles[index],
                    names.getOrNull(index).orEmpty(),
                    intent.getStringArrayListExtra("scheduledTimes")?.getOrNull(index).orEmpty(),
                    planned[index]
                )
            }
            val pendingResult = goAsync()
            Thread {
                try { AlarmScheduler.scheduleNextGroups(context, doses, planned.maxOrNull() ?: System.currentTimeMillis()) }
                finally { pendingResult.finish() }
            }.start()
        }
    }
}
