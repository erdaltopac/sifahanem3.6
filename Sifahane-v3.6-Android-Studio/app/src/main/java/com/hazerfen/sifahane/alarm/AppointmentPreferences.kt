package com.hazerfen.sifahane.alarm

import android.content.Context

object AppointmentPreferences {
    private const val PREFS = "sifahane_appointment_settings"
    private const val ENABLED = "appointment_alarms_enabled"
    private const val DEFAULTS = "appointment_default_reminders"

    fun alarmsEnabled(context: Context): Boolean =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean(ENABLED, true)

    fun setAlarmsEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(ENABLED, enabled).apply()
    }

    fun defaultRemindersCsv(context: Context): String =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(DEFAULTS, "10080,1440,180") ?: "10080,1440,180"

    fun setDefaultRemindersCsv(context: Context, value: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(DEFAULTS, value).apply()
    }
}
