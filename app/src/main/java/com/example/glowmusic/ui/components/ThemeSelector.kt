package com.example.glowmusic.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.glowmusic.domain.model.DarkModePreference
import com.example.glowmusic.domain.model.ThemeSettings
import com.example.glowmusic.domain.model.ThemeStyle
import com.example.glowmusic.ui.theme.Dimens
import com.example.glowmusic.ui.theme.GlowCyan
import com.example.glowmusic.ui.theme.GlowPurple

@Composable
fun ThemeSelector(
    themeSettings: ThemeSettings,
    onStyleSelected: (ThemeStyle) -> Unit,
    onDarkModeSelected: (DarkModePreference) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingSm)
    ) {
        Text(
            text = "Theme Style",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        ThemeStyleCard(
            title = "Material You Dynamic",
            subtitle = "Use Android dynamic colors where available",
            icon = Icons.Default.Palette,
            accentColor = GlowPurple,
            isSelected = themeSettings.themeStyle == ThemeStyle.MATERIAL_YOU,
            onClick = { onStyleSelected(ThemeStyle.MATERIAL_YOU) }
        )

        ThemeStyleCard(
            title = "Dark Neutral",
            subtitle = "Readable charcoal palette",
            icon = Icons.Default.FormatPaint,
            accentColor = Color(0xFF8E8E93),
            isSelected = themeSettings.themeStyle == ThemeStyle.DARK_NEUTRAL,
            onClick = { onStyleSelected(ThemeStyle.DARK_NEUTRAL) }
        )

        ThemeStyleCard(
            title = "Cyberpunk Glow",
            subtitle = "High-contrast neon accents",
            icon = Icons.Default.WbSunny,
            accentColor = GlowCyan,
            isSelected = themeSettings.themeStyle == ThemeStyle.CYBERPUNK_GLOW,
            onClick = { onStyleSelected(ThemeStyle.CYBERPUNK_GLOW) }
        )

        Text(
            text = "Dark Mode",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = Dimens.spacingXs)
        )

        ThemeStyleCard(
            title = "Follow System",
            subtitle = "Use device light/dark setting",
            icon = Icons.Default.Palette,
            accentColor = MaterialTheme.colorScheme.primary,
            isSelected = themeSettings.darkModePreference == DarkModePreference.FOLLOW_SYSTEM,
            onClick = { onDarkModeSelected(DarkModePreference.FOLLOW_SYSTEM) }
        )

        ThemeStyleCard(
            title = "Force Dark",
            subtitle = "Always use dark mode",
            icon = Icons.Default.DarkMode,
            accentColor = Color(0xFF303F9F),
            isSelected = themeSettings.darkModePreference == DarkModePreference.FORCE_DARK,
            onClick = { onDarkModeSelected(DarkModePreference.FORCE_DARK) }
        )

        ThemeStyleCard(
            title = "Force Light",
            subtitle = "Always use light mode",
            icon = Icons.Default.LightMode,
            accentColor = Color(0xFFFFA000),
            isSelected = themeSettings.darkModePreference == DarkModePreference.FORCE_LIGHT,
            onClick = { onDarkModeSelected(DarkModePreference.FORCE_LIGHT) }
        )

        ThemeStyleCard(
            title = "OLED Pitch Black",
            subtitle = "Maximum black background",
            icon = Icons.Default.DarkMode,
            accentColor = Color.Black,
            isSelected = themeSettings.darkModePreference == DarkModePreference.OLED_PITCH_BLACK,
            onClick = { onDarkModeSelected(DarkModePreference.OLED_PITCH_BLACK) }
        )
    }
}

@Composable
private fun ThemeStyleCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor by animateColorAsState(
        if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
        label = "ThemeCardBorder"
    )

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(
                width = if (isSelected) 2.dp else 0.dp,
                color = borderColor,
                shape = RoundedCornerShape(Dimens.cornerRadiusSmallCard)
            ),
        shape = RoundedCornerShape(Dimens.cornerRadiusSmallCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.spacingSm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(Dimens.spacingSm))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isSelected) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
