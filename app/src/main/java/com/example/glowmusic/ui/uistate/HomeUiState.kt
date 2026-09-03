package com.example.glowmusic.ui.uistate

import com.example.glowmusic.domain.model.MediaItem
import com.example.glowmusic.domain.model.Playlist

data class HomeUiState(
    val isLoading: Boolean = false,
    val recentAudio: List<MediaItem> = emptyList(),
    val playlists: List<Playlist> = emptyList(),
    val favoriteTracks: List<MediaItem> = emptyList(),
    val errorMessage: String? = null
)
