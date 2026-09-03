package com.example.glowmusic.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.glowmusic.domain.model.MediaItem
import com.example.glowmusic.domain.usecase.GetMediaLibraryUseCase
import com.example.glowmusic.domain.usecase.ToggleFavoriteUseCase
import com.example.glowmusic.ui.uistate.HomeUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

import kotlinx.coroutines.launch

class HomeViewModel(
    private val getMediaLibraryUseCase: GetMediaLibraryUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            getMediaLibraryUseCase.getAudio().collect { audioList ->
                _uiState.value = HomeUiState(
                    isLoading = false,
                    recentAudio = audioList,
                    favoriteTracks = audioList.filter { it.isFavorite }
                )
            }
        }
    }

    fun toggleFavorite(item: MediaItem) {
        viewModelScope.launch {
            toggleFavoriteUseCase(item.id)
        }
    }
}
