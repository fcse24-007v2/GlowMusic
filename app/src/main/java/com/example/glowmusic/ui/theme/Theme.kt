package com.example.glowmusic.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.glowmusic.domain.model.DarkModePreference
import com.example.glowmusic.domain.model.ThemeSettings
import com.example.glowmusic.domain.model.ThemeStyle

private val NeutralDarkColorScheme = darkColorScheme(
    primary = GlowPurple,
    onPrimary = Color.White,
    background = Color(0xFF121212),
    onBackground = Color(0xFFE0E0E0),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF2C2C2C)
)

private val CyberpunkGlowColorScheme = darkColorScheme(
    primary = Color(0xFFFF007F),
    onPrimary = Color.White,
    secondary = Color(0xFF00F0FF),
    onSecondary = Color.Black,
    background = PitchBlackBase,
    onBackground = Color.White,
    surface = Color(0xFF0A0A12),
    onSurface = Color.White
)

private val FallbackLightColorScheme = lightColorScheme(
    primary = GlowIndigo,
    onPrimary = Color.White,
    background = Color(0xFFF8FAFC),
    onBackground = TextPrimaryLight,
    surface = Color.White,
    onSurface = TextPrimaryLight
)

@Composable
fun GlowMusicTheme(
    themeSettings: ThemeSettings = ThemeSettings(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val systemInDark = isSystemInDarkTheme()

    val isDark = when (themeSettings.darkModePreference) {
        DarkModePreference.FOLLOW_SYSTEM -> systemInDark
        DarkModePreference.FORCE_DARK -> true
        DarkModePreference.FORCE_LIGHT -> false
        DarkModePreference.OLED_PITCH_BLACK -> true
    }

    val colorScheme = when (themeSettings.themeStyle) {
        ThemeStyle.MATERIAL_YOU -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            } else {
                if (isDark) NeutralDarkColorScheme else FallbackLightColorScheme
            }
        }

        ThemeStyle.DARK_NEUTRAL -> if (isDark) NeutralDarkColorScheme else FallbackLightColorScheme
        ThemeStyle.CYBERPUNK_GLOW -> CyberpunkGlowColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
