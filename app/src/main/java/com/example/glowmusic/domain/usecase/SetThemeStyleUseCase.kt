package com.example.glowmusic.domain.usecase

import com.example.glowmusic.domain.model.DarkModePreference
import com.example.glowmusic.domain.model.ThemeStyle
import com.example.glowmusic.domain.repository.ThemeRepository

class SetThemeStyleUseCase(
    private val themeRepository: ThemeRepository
) {
    suspend fun setThemeStyle(style: ThemeStyle) {
        themeRepository.updateThemeStyle(style)
    }

    suspend fun setDarkModePreference(preference: DarkModePreference) {
        themeRepository.updateDarkModePreference(preference)
    }

    suspend fun setDynamicColor(enabled: Boolean) {
        themeRepository.updateDynamicColorEnabled(enabled)
    }
}
