package com.example.glowmusic.ui.uistate

import com.example.glowmusic.domain.model.ThemeSettings

data class ThemeUiState(
    val themeSettings: ThemeSettings = ThemeSettings(),
    val availableThemes: List<String> = listOf(
        "Material You",
        "Dark Neutral",
        "Cyberpunk Glow"
    )
)
