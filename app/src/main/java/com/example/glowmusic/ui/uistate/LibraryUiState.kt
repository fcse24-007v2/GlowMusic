package com.example.glowmusic.ui.uistate

import com.example.glowmusic.domain.model.MediaItem
import com.example.glowmusic.domain.model.Playlist

data class LibraryUiState(
    val audioItems: List<MediaItem> = emptyList(),
    val playlists: List<Playlist> = emptyList(),
    val selectedTab: Int = 0 // 0 = Music, 1 = Playlists
)

data class SearchUiState(
    val searchQuery: String = "",
    val searchResults: List<MediaItem> = emptyList(),
    val isSearching: Boolean = false
)
