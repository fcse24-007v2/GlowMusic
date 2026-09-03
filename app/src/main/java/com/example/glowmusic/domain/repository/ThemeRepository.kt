package com.example.glowmusic.domain.repository

import com.example.glowmusic.domain.model.DarkModePreference
import com.example.glowmusic.domain.model.ThemeSettings
import com.example.glowmusic.domain.model.ThemeStyle
import kotlinx.coroutines.flow.Flow

interface ThemeRepository {
    fun getThemeSettings(): Flow<ThemeSettings>
    suspend fun updateThemeStyle(themeStyle: ThemeStyle)
    suspend fun updateDarkModePreference(preference: DarkModePreference)
    suspend fun updateDynamicColorEnabled(enabled: Boolean)
}
