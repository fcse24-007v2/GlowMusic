package com.example.glowmusic.domain.repository

import com.example.glowmusic.domain.model.MediaItem
import com.example.glowmusic.domain.model.PlaybackState
import com.example.glowmusic.domain.model.RepeatMode
import kotlinx.coroutines.flow.Flow

interface PlaybackRepository {
    fun getPlaybackState(): Flow<PlaybackState>
    fun getQueue(): Flow<List<MediaItem>>
    suspend fun playMedia(item: MediaItem, queue: List<MediaItem> = emptyList())
    suspend fun pause()
    suspend fun resume()
    suspend fun seekTo(positionMs: Long)
    suspend fun skipToNext()
    suspend fun skipToPrevious()
    suspend fun toggleShuffle()
    suspend fun setRepeatMode(mode: RepeatMode)
    suspend fun reorderQueue(fromIndex: Int, toIndex: Int)
}
