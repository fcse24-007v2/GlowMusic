package com.example.glowmusic.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.glowmusic.domain.model.DarkModePreference
import com.example.glowmusic.domain.model.ThemeStyle
import com.example.glowmusic.domain.usecase.GetThemeSettingsUseCase
import com.example.glowmusic.domain.usecase.SetThemeStyleUseCase
import com.example.glowmusic.ui.uistate.ThemeUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ThemeViewModel(
    private val getThemeSettingsUseCase: GetThemeSettingsUseCase,
    private val setThemeStyleUseCase: SetThemeStyleUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ThemeUiState())
    val uiState: StateFlow<ThemeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getThemeSettingsUseCase().collect { settings ->
                _uiState.value = ThemeUiState(themeSettings = settings)
            }
        }
    }

    fun setThemeStyle(style: ThemeStyle) {
        viewModelScope.launch {
            setThemeStyleUseCase.setThemeStyle(style)
        }
    }

    fun setDarkModePreference(preference: DarkModePreference) {
        viewModelScope.launch {
            setThemeStyleUseCase.setDarkModePreference(preference)
        }
    }
}
