package com.example.glowmusic.ui.animation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun ParallaxAlbumArt(
    modifier: Modifier = Modifier,
    isPlaying: Boolean = false,
    content: @Composable () -> Unit
) {
    var scrollOffset by remember { mutableFloatStateOf(0f) }
    val scope = rememberCoroutineScope()

    val parallaxOffset by animateFloatAsState(
        targetValue = if (isPlaying) scrollOffset else 0f,
        animationSpec = AnimationConstants.backgroundFadeSpringSpec,
        label = "parallax_offset"
    )

    val zoomScale by animateFloatAsState(
        targetValue = if (isPlaying) 1.05f else 1f,
        animationSpec = AnimationConstants.backgroundFadeSpringSpec,
        label = "zoom_scale"
    )

    Box(
        modifier = modifier
            .scale(zoomScale)
            .offset(y = parallaxOffset.dp)
            .pointerInput(Unit) {
                // Simple parallax trigger on interaction
                // In practice, this would be driven by scroll or playback progress
            }
    ) {
        content()
    }
}

@Composable
fun SpringScaleButton(
    modifier: Modifier = Modifier,
    isPressed: Boolean = false,
    content: @Composable () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = AnimationConstants.playButtonSpringSpec,
        label = "button_scale"
    )

    Box(
        modifier = modifier.scale(scale)
    ) {
        content()
    }
}

@Composable
fun FadeTransition(
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    content: @Composable () -> Unit
) {
    val opacity by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = AnimationConstants.backgroundFadeSpringSpec,
        label = "fade"
    )

    Box(modifier = modifier) {
        content()
    }
}
