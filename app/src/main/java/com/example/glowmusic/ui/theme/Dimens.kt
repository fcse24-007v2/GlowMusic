package com.example.glowmusic.ui.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object Dimens {
    // Spacing scale: 8 / 16 / 24 / 32 / 40 / 48 / 64 dp
    val spacingXs: Dp = 8.dp
    val spacingSm: Dp = 16.dp
    val spacingMd: Dp = 24.dp
    val spacingLg: Dp = 32.dp
    val spacingXl: Dp = 40.dp
    val spacing2Xl: Dp = 48.dp
    val spacing3Xl: Dp = 64.dp

    // Corner Radius - Shape Scale
    val cornerRadiusExtraSmall: Dp = 4.dp      // Tight, precise elements
    val cornerRadiusSmall: Dp = 8.dp           // Buttons, small components
    val cornerRadiusMedium: Dp = 16.dp         // Standard cards
    val cornerRadiusScreenCard: Dp = 28.dp     // Large surfaces
    val cornerRadiusSmallCard: Dp = 24.dp      // Medium cards
    val cornerRadiusButton: Dp = 24.dp         // Buttons
    val cornerRadiusLarge: Dp = 36.dp          // Hero sections

    // Elevation Scale - Material 3 Expressive
    val elevationLevel0: Dp = 0.dp              // Flat surfaces
    val elevationLevel1: Dp = 1.dp              // Cards, chips
    val elevationLevel2: Dp = 3.dp              // Floating content
    val elevationLevel3: Dp = 6.dp              // FABs, emphasized
    val elevationLevel4: Dp = 8.dp              // Modals, sheets
    val elevationLevel5: Dp = 12.dp             // Hero elements

    // Legacy compatibility
    val subtleElevation: Dp = elevationLevel2

    // Animation Timings (250-350ms spring/tween)
    const val animationDurationMs: Int = 300
}
