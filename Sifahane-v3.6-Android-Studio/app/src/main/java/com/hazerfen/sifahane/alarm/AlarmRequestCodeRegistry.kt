package com.hazerfen.sifahane.alarm

import android.content.Context

internal object AlarmRequestCodeRegistry {
    private const val PREFS = "sifahane_alarm_request_codes_v1"
    private const val CODE_PREFIX = "code:"

    @Synchronized
    fun getOrAllocate(context: Context, groupKey: String): Int {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val key = CODE_PREFIX + groupKey
        val existing = prefs.getInt(key, 0)
        if (existing > 0) return existing

        val occupied = prefs.all.mapNotNull { (storedKey, value) ->
            if (!storedKey.startsWith(CODE_PREFIX)) return@mapNotNull null
            val code = value as? Int ?: return@mapNotNull null
            code to storedKey.removePrefix(CODE_PREFIX)
        }.toMap()
        val allocated = AlarmIdentity.allocate(groupKey, occupied)
        check(prefs.edit().putInt(key, allocated).commit()) {
            "Alarm istek kodu kaydedilemedi."
        }
        return allocated
    }

    fun find(context: Context, groupKey: String): Int? =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getInt(CODE_PREFIX + groupKey, 0)
            .takeIf { it > 0 }

    fun release(context: Context, groupKey: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .remove(CODE_PREFIX + groupKey)
            .apply()
    }

    fun clear(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().clear().apply()
    }
}
