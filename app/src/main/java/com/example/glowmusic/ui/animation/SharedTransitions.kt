package com.example.glowmusic.ui.animation

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun AnimatedAlbumArt(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    size: Float = 56f,
    isExpanded: Boolean = false,
    animationSpec: AnimationSpec<Dp> = spring(),
    contentScale: ContentScale = ContentScale.Crop
) {
    val animatedSize by animateDpAsState(
        targetValue = if (isExpanded) 300.dp else size.dp,
        animationSpec = animationSpec,
        label = "album_art_size"
    )

    val cornerRadius by animateDpAsState(
        targetValue = if (isExpanded) 16.dp else 8.dp,
        animationSpec = animationSpec,
        label = "album_art_corner"
    )

    Box(
        modifier = modifier
            .size(animatedSize)
            .clip(RoundedCornerShape(cornerRadius))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "Album Art",
            contentScale = contentScale,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun AnimatedPlayerBackground(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    opacity: Float = 0.3f
) {
    val alpha by animateFloatAsState(
        targetValue = if (isVisible) opacity else 0f,
        animationSpec = AnimationConstants.backgroundFadeSpringSpec,
        label = "bg_opacity"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.surface.copy(alpha = alpha)
            )
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "Player Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(0.dp)),
            alpha = alpha
        )
    }
}

