package com.example.glowmusic.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.glowmusic.domain.model.MediaItem
import com.example.glowmusic.domain.repository.PlaybackRepository
import com.example.glowmusic.domain.usecase.GetQueueUseCase
import com.example.glowmusic.domain.usecase.ParseSyncedLyricsUseCase

import com.example.glowmusic.ui.uistate.PlayerUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playbackRepository: PlaybackRepository,
    private val getQueueUseCase: GetQueueUseCase,
    private val parseSyncedLyricsUseCase: ParseSyncedLyricsUseCase = ParseSyncedLyricsUseCase()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    private var cachedLyricsMediaId: Long? = null
    private var cachedLyrics = emptyList<com.example.glowmusic.domain.model.LyricLine>()

    init {
        observePlayback()
    }

    private fun observePlayback() {
        viewModelScope.launch {
            combine(
                playbackRepository.getPlaybackState(),
                getQueueUseCase()
            ) { state, queue ->
                val currentMediaId = state.currentItem?.id
                if (currentMediaId != cachedLyricsMediaId) {
                    cachedLyricsMediaId = currentMediaId
                    cachedLyrics = parseSyncedLyricsUseCase.parse(state.currentItem?.embeddedLyrics)
                }

                val currentLineIndex = parseSyncedLyricsUseCase.findCurrentLineIndex(
                    lines = cachedLyrics,
                    playbackPositionMs = state.currentPositionMs
                )
                val currentWordIndex = parseSyncedLyricsUseCase.findCurrentWordIndex(
                    line = cachedLyrics.getOrNull(currentLineIndex),
                    playbackPositionMs = state.currentPositionMs
                )

                PlayerUiState(
                    playbackState = state,
                    queue = queue,
                    lyrics = cachedLyrics,
                    currentLyricLineIndex = currentLineIndex,
                    currentLyricWordIndex = currentWordIndex
                )
            }.collect { newState ->
                _uiState.value = newState
            }
        }
    }

    fun playMedia(item: MediaItem, queue: List<MediaItem> = emptyList()) {
        viewModelScope.launch {
            playbackRepository.playMedia(item, queue)
        }
    }

    fun togglePlayPause() {
        viewModelScope.launch {
            if (_uiState.value.playbackState.isPlaying) {
                playbackRepository.pause()
            } else {
                playbackRepository.resume()
            }
        }
    }

    fun seekTo(positionMs: Long) {
        viewModelScope.launch {
            playbackRepository.seekTo(positionMs)
        }
    }

    fun skipToNext() {
        viewModelScope.launch {
            playbackRepository.skipToNext()
        }
    }

    fun skipToPrevious() {
        viewModelScope.launch {
            playbackRepository.skipToPrevious()
        }
    }

    fun toggleShuffle() {
        viewModelScope.launch {
            playbackRepository.toggleShuffle()
        }
    }
}
