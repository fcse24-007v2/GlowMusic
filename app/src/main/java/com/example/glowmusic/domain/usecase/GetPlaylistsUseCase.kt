package com.example.glowmusic.domain.usecase

import com.example.glowmusic.domain.model.MediaItem
import com.example.glowmusic.domain.model.Playlist
import com.example.glowmusic.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow

class GetPlaylistsUseCase(private val playlistRepository: PlaylistRepository) {

    fun getAllPlaylists(): Flow<List<Playlist>> = playlistRepository.getAllPlaylists()

    fun getPlaylistTracks(playlistId: Long): Flow<List<MediaItem>> = playlistRepository.getPlaylistTracks(playlistId)

}
