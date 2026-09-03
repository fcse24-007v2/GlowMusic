package com.example.glowmusic.data.repository

import com.example.glowmusic.data.local.db.dao.PlaylistDao
import com.example.glowmusic.data.local.db.entity.PlaylistEntity
import com.example.glowmusic.data.local.db.entity.PlaylistTrackCrossRef
import com.example.glowmusic.domain.model.MediaItem
import com.example.glowmusic.domain.model.Playlist
import com.example.glowmusic.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao
) : PlaylistRepository {

    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return playlistDao.getAllPlaylists().map { entities ->
            entities.map { entity ->
                val trackCount = playlistDao.getTrackCountForPlaylist(entity.id)
                entity.toDomain(trackCount)
            }
        }
    }

    override fun getPlaylistTracks(playlistId: Long): Flow<List<MediaItem>> {
        return playlistDao.getPlaylistTracks(playlistId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun createPlaylist(name: String): Long {
        val entity = PlaylistEntity(name = name)
        return playlistDao.insertPlaylist(entity)
    }

    override suspend fun deletePlaylist(playlistId: Long) {
        playlistDao.deletePlaylist(playlistId)
    }

    override suspend fun addTrackToPlaylist(playlistId: Long, trackId: Long) {
        playlistDao.addTrackToPlaylist(PlaylistTrackCrossRef(playlistId, trackId))
    }

    override suspend fun removeTrackFromPlaylist(playlistId: Long, trackId: Long) {
        playlistDao.removeTrackFromPlaylist(playlistId, trackId)
    }
}
