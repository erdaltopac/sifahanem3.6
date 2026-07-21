package com.hazerfen.sifahane.ui

import android.content.Context

data class ThemeConfiguration(
    val preset: String = "default",
    val font: String = "roboto",
    val fontScale: Float = 1f,
    val accentOpacity: Float = 1f
)

object ThemePreferences {
    private const val FILE = "sifahane_theme"
    fun load(context: Context): ThemeConfiguration {
        val prefs = context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
        return ThemeConfiguration(
            preset = prefs.getString("preset", "default") ?: "default",
            font = prefs.getString("font", "roboto") ?: "roboto",
            fontScale = prefs.getFloat("font_scale", 1f).coerceIn(.85f, 1.35f),
            accentOpacity = prefs.getFloat("accent_opacity", 1f).coerceIn(.35f, 1f)
        )
    }

    fun save(context: Context, config: ThemeConfiguration) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit()
            .putString("preset", config.preset)
            .putString("font", config.font)
            .putFloat("font_scale", config.fontScale.coerceIn(.85f, 1.35f))
            .putFloat("accent_opacity", config.accentOpacity.coerceIn(.35f, 1f))
            .apply()
    }

    fun reset(context: Context) = context.getSharedPreferences(FILE, Context.MODE_PRIVATE).edit().clear().apply()
}
