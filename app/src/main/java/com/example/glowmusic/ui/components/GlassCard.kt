package com.example.glowmusic.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.glowmusic.ui.theme.Dimens

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(Dimens.cornerRadiusSmallCard),
    elevation: Dp = Dimens.subtleElevation,
    opacity: Float? = null,
    borderColor: Color = Color.Transparent,
    content: @Composable BoxScope.() -> Unit
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val effectiveBorder = if (borderColor == Color.Transparent) {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
    } else {
        borderColor
    }

    Box(
        modifier = modifier
            .background(surfaceColor, shape)
            .border(width = 1.dp, color = effectiveBorder, shape = shape),
        content = content
    )
}
