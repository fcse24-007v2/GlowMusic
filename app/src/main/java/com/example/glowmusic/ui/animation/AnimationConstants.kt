package com.example.glowmusic.ui.animation

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring

object AnimationConstants {
    // Spring specifications for different use cases
    val playButtonSpringSpec = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessHigh
    )

    val queueSheetSpringSpec = spring<Float>(
        dampingRatio = Spring.DampingRatioLowBouncy,
        stiffness = Spring.StiffnessMedium
    )

    val backgroundFadeSpringSpec = spring<Float>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMediumLow
    )

    val miniPlayerSpringSpec = spring<Float>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )

    // Duration for transitions (in milliseconds)
    const val MINI_TO_FULL_PLAYER_DURATION = 400
    const val ALBUM_ART_PARALLAX_DURATION = 3000
    const val STANDARD_ANIMATION_DURATION = 300
}
