package com.example.glowmusic.data.repository

import com.example.glowmusic.data.local.scanner.MediaStoreScanner
import com.example.glowmusic.domain.model.MediaItem
import com.example.glowmusic.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class InMemoryMediaRepository(
    private val mediaStoreScanner: MediaStoreScanner
) : MediaRepository {

    private val mediaItems = MutableStateFlow<List<MediaItem>>(emptyList())

    override fun getAllMedia(): Flow<List<MediaItem>> = mediaItems.asStateFlow()

    override fun getAudioMedia(): Flow<List<MediaItem>> = mediaItems.asStateFlow()

    override fun getFavoriteMedia(): Flow<List<MediaItem>> = mediaItems.map { list ->
        list.filter { it.isFavorite }
    }

    override fun searchMedia(query: String): Flow<List<MediaItem>> = mediaItems.map { list ->
        if (query.isBlank()) {
            emptyList()
        } else {
            list.filter {
                it.title.contains(query, ignoreCase = true) ||
                    it.artist.contains(query, ignoreCase = true) ||
                    it.album.contains(query, ignoreCase = true)
            }
        }
    }

    override suspend fun getMediaById(id: Long): MediaItem? {
        return mediaItems.value.find { it.id == id }
    }

    override suspend fun toggleFavorite(mediaId: Long) {
        mediaItems.update { list ->
            list.map { item ->
                if (item.id == mediaId) item.copy(isFavorite = !item.isFavorite) else item
            }
        }
    }

    override suspend fun scanMediaStore() {
        val scannedItems = mediaStoreScanner.scanOnDeviceMedia()
        mediaItems.value = scannedItems
    }
}
