package com.example.glowmusic.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.glowmusic.domain.model.DarkModePreference
import com.example.glowmusic.domain.model.ThemeStyle
import com.example.glowmusic.ui.components.ThemeSelector
import com.example.glowmusic.ui.theme.Dimens
import com.example.glowmusic.ui.uistate.ThemeUiState

@Composable
fun SettingsScreen(
    themeUiState: ThemeUiState,
    onStyleSelected: (ThemeStyle) -> Unit,
    onDarkModeSelected: (DarkModePreference) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Dimens.spacingMd, vertical = Dimens.spacingMd)
            .padding(bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingLg)
    ) {
        Text(
            text = "Settings & Appearance",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary
        )

        ThemeSelector(
            themeSettings = themeUiState.themeSettings,
            onStyleSelected = onStyleSelected,
            onDarkModeSelected = onDarkModeSelected
        )
    }
}
