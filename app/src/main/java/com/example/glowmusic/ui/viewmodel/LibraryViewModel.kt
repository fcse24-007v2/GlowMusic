package com.example.glowmusic.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.glowmusic.domain.model.MediaItem
import com.example.glowmusic.domain.usecase.GetMediaLibraryUseCase
import com.example.glowmusic.domain.usecase.GetPlaylistsUseCase
import com.example.glowmusic.domain.usecase.SearchMediaUseCase
import com.example.glowmusic.ui.uistate.LibraryUiState
import com.example.glowmusic.ui.uistate.SearchUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class LibraryViewModel(
    private val getMediaLibraryUseCase: GetMediaLibraryUseCase,
    private val getPlaylistsUseCase: GetPlaylistsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                getMediaLibraryUseCase.getAudio(),
                getPlaylistsUseCase.getAllPlaylists()
            ) { audio, playlists ->
                LibraryUiState(
                    audioItems = audio,
                    playlists = playlists,
                    selectedTab = _uiState.value.selectedTab
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun setSelectedTab(index: Int) {
        _uiState.value = _uiState.value.copy(selectedTab = index)
    }

    fun getPlaylistTracksFlow(playlistId: Long): Flow<List<MediaItem>> =
        getPlaylistsUseCase.getPlaylistTracks(playlistId)
}

class SearchViewModel(
    private val searchMediaUseCase: SearchMediaUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query, isSearching = query.isNotBlank())
        if (query.isNotBlank()) {
            viewModelScope.launch {
                searchMediaUseCase(query).collect { results ->
                    _uiState.value = _uiState.value.copy(searchResults = results, isSearching = false)
                }
            }
        } else {
            _uiState.value = _uiState.value.copy(searchResults = emptyList(), isSearching = false)
        }
    }
}
