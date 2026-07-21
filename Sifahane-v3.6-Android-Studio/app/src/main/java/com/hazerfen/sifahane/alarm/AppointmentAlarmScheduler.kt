package com.hazerfen.sifahane.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import com.hazerfen.sifahane.data.Appointment

object AppointmentAlarmScheduler {
    private const val ACTION = "com.hazerfen.sifahane.APPOINTMENT_ALARM"

    fun schedule(context: Context, appointment: Appointment) {
        cancel(context, appointment.id)
        if (!appointment.active || appointment.status != "PLANNED") return
        if (!AppointmentPreferences.alarmsEnabled(context)) return

        reminderMinutes(appointment).forEachIndexed { index, minutes ->
            val triggerAt = appointment.appointmentDateTime - minutes * 60_000L
            if (triggerAt <= System.currentTimeMillis()) return@forEachIndexed
            val identity = identity(appointment.id, index)
            val intent = alarmIntent(context, identity).apply {
                putExtra("appointmentId", appointment.id)
                putExtra("profileId", appointment.profileId)
                putExtra("doctorName", appointment.doctorName)
                putExtra("branch", appointment.branch)
                putExtra("institution", appointment.institution)
                putExtra("clinic", appointment.clinic)
                putExtra("appointmentDateTime", appointment.appointmentDateTime)
                putExtra("reminderMinutes", minutes)
                putExtra("alarmIdentity", identity)
            }
            val pending = PendingIntent.getBroadcast(
                context,
                AlarmRequestCodeRegistry.getOrAllocate(context, identity),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            setAlarm(context.getSystemService(AlarmManager::class.java), triggerAt, pending)
        }
    }

    fun cancel(context: Context, appointmentId: Long) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        for (index in 0..12) {
            val identity = identity(appointmentId, index)
            val code = AlarmRequestCodeRegistry.find(context, identity) ?: continue
            PendingIntent.getBroadcast(
                context,
                code,
                alarmIntent(context, identity),
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )?.let {
                alarmManager.cancel(it)
                it.cancel()
            }
            AlarmRequestCodeRegistry.release(context, identity)
        }
    }

    fun notificationId(context: Context, appointmentId: Long): Int =
        AlarmRequestCodeRegistry.getOrAllocate(context, "APPOINTMENT_NOTIFICATION:$appointmentId")

    fun reminderMinutes(appointment: Appointment): List<Int> =
        appointment.remindersCsv.split(",")
            .mapNotNull { it.trim().toIntOrNull() }
            .filter { it > 0 }
            .distinct()
            .sortedDescending()

    private fun identity(id: Long, index: Int) = "APPOINTMENT:$id:$index"

    private fun alarmIntent(context: Context, identity: String) =
        Intent(context, AppointmentAlarmReceiver::class.java).apply {
            action = ACTION
            data = Uri.parse("sifahane://appointment-alarm/$identity")
        }

    private fun setAlarm(
        alarmManager: AlarmManager,
        triggerAt: Long,
        pendingIntent: PendingIntent
    ) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
                !alarmManager.canScheduleExactAlarms()
            ) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
            }
        } catch (_: SecurityException) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
        }
    }
}
