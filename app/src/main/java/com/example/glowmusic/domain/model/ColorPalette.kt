package com.example.glowmusic.domain.model

data class ColorPalette(
    val dominantColor: Long = 0xFF1E1B2E,
    val vibrantColor: Long = 0xFF6366F1,
    val mutedColor: Long = 0xFF3B3B4F,
    val textPrimary: Long = 0xFFFFFFFF,
    val textSecondary: Long = 0xFFB0B0C0,
    val isDark: Boolean = true
)
