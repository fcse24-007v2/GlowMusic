package com.example.glowmusic.domain.usecase

import com.example.glowmusic.domain.model.ThemeSettings
import com.example.glowmusic.domain.repository.ThemeRepository
import kotlinx.coroutines.flow.Flow

class GetThemeSettingsUseCase(
    private val themeRepository: ThemeRepository
) {
    operator fun invoke(): Flow<ThemeSettings> {
        return themeRepository.getThemeSettings()
    }
}
