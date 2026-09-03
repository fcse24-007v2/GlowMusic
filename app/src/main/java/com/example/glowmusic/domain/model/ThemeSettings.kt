package com.example.glowmusic.domain.model

enum class ThemeStyle {
    MATERIAL_YOU,
    DARK_NEUTRAL,
    CYBERPUNK_GLOW
}

enum class DarkModePreference {
    FOLLOW_SYSTEM,
    FORCE_DARK,
    FORCE_LIGHT,
    OLED_PITCH_BLACK
}

data class ThemeSettings(
    val themeStyle: ThemeStyle = ThemeStyle.DARK_NEUTRAL,
    val darkModePreference: DarkModePreference = DarkModePreference.FOLLOW_SYSTEM,
    val useDynamicColorFromAlbumArt: Boolean = true,
    val enableSpringAnimations: Boolean = true
)
