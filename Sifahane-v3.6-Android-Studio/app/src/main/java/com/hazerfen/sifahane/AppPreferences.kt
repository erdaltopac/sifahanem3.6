package com.hazerfen.sifahane

import android.content.Context

object AppPreferences {
    private const val PREF = "sifahane_app_preferences"
    private const val SNOOZE_1 = "snooze_minutes_1"
    private const val SNOOZE_2 = "snooze_minutes_2"
    private const val SNOOZE_3 = "snooze_minutes_3"
    private const val LOCK_TIMEOUT = "lock_timeout_millis"
    private const val ALARM_RING_DURATION = "alarm_ring_duration_millis"
    private const val LAST_VERIFIED_VERSION = "last_verified_version"
    private const val INSTALL_SETUP_DONE = "install_setup_done"

    fun snoozeMinutes(context: Context): List<Int> {
        val p = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        return listOf(p.getInt(SNOOZE_1, 5), p.getInt(SNOOZE_2, 10), p.getInt(SNOOZE_3, 15))
            .map { it.coerceIn(1, 180) }
    }

    fun saveSnoozeMinutes(context: Context, values: List<Int>) {
        require(validSnoozeMinutes(values))
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit()
            .putInt(SNOOZE_1, values[0]).putInt(SNOOZE_2, values[1]).putInt(SNOOZE_3, values[2]).apply()
    }

    fun lockTimeoutMillis(context: Context): Long =
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE).getLong(LOCK_TIMEOUT, 120_000L)

    fun saveLockTimeoutMillis(context: Context, value: Long) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit().putLong(LOCK_TIMEOUT, value).apply()
    }

    fun alarmRingDurationMillis(context: Context): Long =
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .getLong(ALARM_RING_DURATION, 120_000L)

    fun saveAlarmRingDurationMillis(context: Context, value: Long) {
        require(value in 30_000L..600_000L)
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit()
            .putLong(ALARM_RING_DURATION, value).apply()
    }

    fun requiresVersionVerification(context: Context, versionCode: Int): Boolean =
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE).getInt(LAST_VERIFIED_VERSION, -1) != versionCode

    fun markVersionVerified(context: Context, versionCode: Int) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit().putInt(LAST_VERIFIED_VERSION, versionCode).apply()
    }

    fun installSetupDone(context: Context): Boolean =
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE).getBoolean(INSTALL_SETUP_DONE, false)

    fun markInstallSetupDone(context: Context) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit().putBoolean(INSTALL_SETUP_DONE, true).apply()
    }

    fun recordSettingsUse(context: Context, key: String) {
        val prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        prefs.edit()
            .putString("settings_last_used", key)
            .putInt("settings_use_$key", prefs.getInt("settings_use_$key", 0) + 1)
            .apply()
    }

    fun lastUsedSetting(context: Context): String? =
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE).getString("settings_last_used", null)

    fun frequentlyUsedSettings(context: Context, keys: List<String>, limit: Int = 3): List<String> {
        val prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        return keys.sortedByDescending { prefs.getInt("settings_use_$it", 0) }
            .filter { prefs.getInt("settings_use_$it", 0) > 0 }
            .take(limit)
    }

    fun clearSettingsUsage(context: Context) {
        val prefs = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        val editor = prefs.edit().remove("settings_last_used")
        prefs.all.keys.filter { it.startsWith("settings_use_") }.forEach(editor::remove)
        editor.apply()
    }
}

internal fun validSnoozeMinutes(values: List<Int>): Boolean =
    values.size == 3 && values.all { it in 1..180 } && values.distinct().size == 3
