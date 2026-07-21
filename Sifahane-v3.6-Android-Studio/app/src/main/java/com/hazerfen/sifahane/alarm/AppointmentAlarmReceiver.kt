package com.hazerfen.sifahane.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.hazerfen.sifahane.MainActivity
import com.hazerfen.sifahane.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AppointmentAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val manager = context.getSystemService(NotificationManager::class.java)
        val channelId = "sifahane_appointments"
        manager.createNotificationChannel(
            NotificationChannel(
                channelId,
                "Doktor Randevuları",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Yaklaşan doktor randevusu hatırlatmaları"
                enableVibration(true)
            }
        )

        val appointmentId = intent.getLongExtra("appointmentId", 0L)
        val doctor = intent.getStringExtra("doctorName").orEmpty()
        val branch = intent.getStringExtra("branch").orEmpty()
        val institution = intent.getStringExtra("institution").orEmpty()
        val clinic = intent.getStringExtra("clinic").orEmpty()
        val dateTime = intent.getLongExtra("appointmentDateTime", 0L)
        val formatted = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale("tr", "TR"))
            .format(Date(dateTime))

        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("openAppointments", true)
        }
        val notificationId = AppointmentAlarmScheduler.notificationId(context, appointmentId)
        val openPending = PendingIntent.getActivity(
            context,
            notificationId,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = if (doctor.isBlank()) "Doktor randevusu yaklaşıyor" else "$doctor randevusu"
        val details = listOf(branch, institution, clinic, formatted)
            .filter { it.isNotBlank() }
            .joinToString(" • ")

        manager.notify(
            notificationId,
            NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_sifahane)
                .setContentTitle(title)
                .setContentText(details)
                .setStyle(NotificationCompat.BigTextStyle().bigText(details))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setAutoCancel(true)
                .setContentIntent(openPending)
                .build()
        )
    }
}
