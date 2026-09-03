package com.example.glowmusic.data.repository

import com.example.glowmusic.domain.model.MediaItem
import com.example.glowmusic.domain.model.Playlist
import com.example.glowmusic.domain.repository.MediaRepository
import com.example.glowmusic.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class InMemoryPlaylistRepository(
    private val mediaRepository: MediaRepository
) : PlaylistRepository {

    private val playlists = MutableStateFlow<List<Playlist>>(emptyList())
    private val playlistTracks = MutableStateFlow<Map<Long, List<MediaItem>>>(emptyMap())
    private var nextPlaylistId = 1L

    override fun getAllPlaylists(): Flow<List<Playlist>> = playlists

    override fun getPlaylistTracks(playlistId: Long): Flow<List<MediaItem>> {
        return playlistTracks.map { map -> map[playlistId].orEmpty() }
    }

    override suspend fun createPlaylist(name: String): Long {
        val id = nextPlaylistId++
        playlists.update { current ->
            (current + Playlist(id = id, name = name)).sortedBy { it.name }
        }
        return id
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        playlists.update { current -> current.filterNot { it.id == playlistId } }
        playlistTracks.update { current -> current - playlistId }
    }

    override suspend fun addTrackToPlaylist(playlistId: Long, trackId: Long) {
        val item = mediaRepository.getMediaById(trackId) ?: return
        playlistTracks.update { current ->
            val currentTracks = current[playlistId].orEmpty()
            if (currentTracks.any { it.id == trackId }) {
                current
            } else {
                current + (playlistId to (currentTracks + item))
            }
        }
        syncTrackCount(playlistId)
    }

    override suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long) {
        playlistTracks.update { current ->
            val updated = current[playlistId].orEmpty().filterNot { it.id == trackId }
            current + (playlistId to updated)
        }
        syncTrackCount(playlistId)
    }

    private fun syncTrackCount(playlistId: Long) {
        val count = playlistTracks.value[playlistId]?.size ?: 0
        playlists.update { current ->
            current.map { playlist ->
                if (playlist.id == playlistId) playlist.copy(trackCount = count) else playlist
            }
        }
    }
}
