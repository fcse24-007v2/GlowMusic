package com.example.glowmusic.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
    shape: Shape = androidx.compose.foundation.shape.RoundedCornerShape(Dimens.cornerRadiusSmallCard),
    elevation: Dp = Dimens.elevationLevel1,
    opacity: Float? = null,
    borderColor: Color = Color.Transparent,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    content: @Composable BoxScope.() -> Unit
) {
    val effectiveBorder = if (borderColor == Color.Transparent) {
        MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
    } else {
        borderColor
    }

    Card(
        modifier = modifier,
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevation,
            pressedElevation = elevation + 2.dp
        )
    ) {
        Box(
            modifier = Modifier
                .border(width = 0.5.dp, color = effectiveBorder, shape = shape),
            content = content
        )
    }
}

