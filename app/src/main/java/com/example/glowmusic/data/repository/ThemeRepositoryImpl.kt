package com.example.glowmusic.data.repository

import com.example.glowmusic.data.local.datastore.ThemePreferences
import com.example.glowmusic.domain.model.DarkModePreference
import com.example.glowmusic.domain.model.ThemeSettings
import com.example.glowmusic.domain.model.ThemeStyle
import com.example.glowmusic.domain.repository.ThemeRepository
import kotlinx.coroutines.flow.Flow

class ThemeRepositoryImpl(
    private val themePreferences: ThemePreferences
) : ThemeRepository {

    override fun getThemeSettings(): Flow<ThemeSettings> {
        return themePreferences.themeSettingsFlow
    }

    override suspend fun updateThemeStyle(themeStyle: ThemeStyle) {
        themePreferences.setThemeStyle(themeStyle)
    }

    override suspend fun updateDarkModePreference(preference: DarkModePreference) {
        themePreferences.setDarkModePreference(preference)
    }

    override suspend fun updateDynamicColorEnabled(enabled: Boolean) {
        themePreferences.setDynamicColor(enabled)
    }
}
