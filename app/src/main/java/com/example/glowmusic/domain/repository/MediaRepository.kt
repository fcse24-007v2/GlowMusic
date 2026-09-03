package com.example.glowmusic.domain.repository

import com.example.glowmusic.domain.model.MediaItem
import kotlinx.coroutines.flow.Flow

interface MediaRepository {
    fun getAllMedia(): Flow<List<MediaItem>>
    fun getAudioMedia(): Flow<List<MediaItem>>
    fun getFavoriteMedia(): Flow<List<MediaItem>>
    fun searchMedia(query: String): Flow<List<MediaItem>>
    suspend fun getMediaById(id: Long): MediaItem?
    suspend fun toggleFavorite(mediaId: Long)
    suspend fun scanMediaStore()
}
