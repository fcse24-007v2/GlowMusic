package com.example.glowmusic.data.repository

import com.example.glowmusic.data.local.db.dao.TrackDao
import com.example.glowmusic.data.local.db.entity.TrackEntity
import com.example.glowmusic.data.local.scanner.MediaStoreScanner
import com.example.glowmusic.domain.model.MediaItem
import com.example.glowmusic.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MediaRepositoryImpl(
    private val trackDao: TrackDao,
    private val mediaStoreScanner: MediaStoreScanner
) : MediaRepository {

    override fun getAllMedia(): Flow<List<MediaItem>> {
        return trackDao.getAllTracks().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getAudioMedia(): Flow<List<MediaItem>> {
        return trackDao.getAudioTracks().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getFavoriteMedia(): Flow<List<MediaItem>> {
        return trackDao.getFavoriteTracks().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun searchMedia(query: String): Flow<List<MediaItem>> {
        return trackDao.searchTracks(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getMediaById(id: Long): MediaItem? {
        val entity = trackDao.getTrackById(id)
        return entity?.toDomain()
    }

    override suspend fun toggleFavorite(mediaId: Long) {
        trackDao.toggleFavorite(mediaId)
    }

    override suspend fun scanMediaStore() {
        val scannedItems = mediaStoreScanner.scanOnDeviceMedia()
        val entities = scannedItems.map { TrackEntity.fromDomain(it) }
        if (entities.isNotEmpty()) {
            trackDao.insertOrUpdateTracks(entities)
        }
    }
}
