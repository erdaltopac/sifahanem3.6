package com.hazerfen.sifahane.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.platform.LocalContext
import com.hazerfen.sifahane.R

private val LogoColor = Color(0xFF72D4CD)
private val Vantablack = Color(0xFF050505)
private val TurkishRed = Color(0xFFE30A17)
private val TurkishBlue = Color(0xFF00AEEF)

private fun sifahanePalette(config: ThemeConfiguration): androidx.compose.material3.ColorScheme {
    val accent = when (config.preset) {
        "turkish_blue" -> TurkishBlue
        "turkish_red" -> TurkishRed
        else -> LogoColor
    }
    val themedAccent = accent.copy(alpha = config.accentOpacity)
    val softAccent = accent.copy(alpha = 0.12f * config.accentOpacity)
    return lightColorScheme(
    primary = themedAccent,
    onPrimary = Vantablack,
    primaryContainer = softAccent,
    onPrimaryContainer = Vantablack,
    inversePrimary = themedAccent,

    secondary = themedAccent,
    onSecondary = Vantablack,
    secondaryContainer = softAccent,
    onSecondaryContainer = Vantablack,

    tertiary = themedAccent,
    onTertiary = Vantablack,
    tertiaryContainer = softAccent,
    onTertiaryContainer = Vantablack,

    surface = if (config.preset == "soft") Color(0xFFF8FBFA) else Color.White,
    onSurface = Vantablack,
    surfaceVariant = softAccent,
    onSurfaceVariant = Vantablack.copy(alpha = 0.72f),
    background = Color.White,
    onBackground = Vantablack,

    outline = Vantablack.copy(alpha = 0.55f),
    outlineVariant = accent.copy(alpha = 0.45f * config.accentOpacity),
    error = TurkishRed,
    onError = Color.White,
    errorContainer = TurkishRed.copy(alpha = 0.10f),
    onErrorContainer = TurkishRed
    )
}

private fun sifahaneTypography(config: ThemeConfiguration) = Typography().let { base ->
    val family = when (config.font) {
        "noto_sans" -> FontFamily(Font(R.font.noto_sans))
        "atkinson" -> FontFamily(Font(R.font.atkinson_hyperlegible))
        "lexend" -> FontFamily(Font(R.font.lexend))
        "noto_serif" -> FontFamily(Font(R.font.noto_serif))
        else -> FontFamily(Font(R.font.roboto))
    }
    fun TextStyle.unified() = copy(fontFamily = family, fontSize = fontSize * config.fontScale)
    base.copy(
        displayLarge = base.displayLarge.unified(), displayMedium = base.displayMedium.unified(),
        displaySmall = base.displaySmall.unified(), headlineLarge = base.headlineLarge.unified(),
        headlineMedium = base.headlineMedium.unified(), headlineSmall = base.headlineSmall.unified(),
        titleLarge = base.titleLarge.unified(), titleMedium = base.titleMedium.unified(),
        titleSmall = base.titleSmall.unified(), bodyLarge = base.bodyLarge.unified(),
        bodyMedium = base.bodyMedium.unified(), bodySmall = base.bodySmall.unified(),
        labelLarge = base.labelLarge.unified(), labelMedium = base.labelMedium.unified(),
        labelSmall = base.labelSmall.unified()
    )
}

@Composable
fun SifahaneTheme(content: @Composable () -> Unit) {
    val config = ThemePreferences.load(LocalContext.current)
    MaterialTheme(
        colorScheme = sifahanePalette(config),
        typography = sifahaneTypography(config),
        content = content
    )
}
