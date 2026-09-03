package com.example.glowmusic.data.repository

import com.example.glowmusic.domain.model.MediaItem
import com.example.glowmusic.domain.model.PlaybackState
import com.example.glowmusic.domain.model.RepeatMode
import com.example.glowmusic.domain.repository.PlaybackRepository
import com.example.glowmusic.playback.GlowMediaControllerManager
import kotlinx.coroutines.flow.Flow

class PlaybackRepositoryImpl(
    private val controllerManager: GlowMediaControllerManager
) : PlaybackRepository {

    override fun getPlaybackState(): Flow<PlaybackState> {
        return controllerManager.playbackState
    }

    override fun getQueue(): Flow<List<MediaItem>> {
        return controllerManager.queue
    }

    override suspend fun playMedia(item: MediaItem, queue: List<MediaItem>) {
        controllerManager.playMedia(item, queue)
    }

    override suspend fun pause() {
        controllerManager.pause()
    }

    override suspend fun resume() {
        controllerManager.resume()
    }

    override suspend fun seekTo(positionMs: Long) {
        controllerManager.seekTo(positionMs)
    }

    override suspend fun skipToNext() {
        controllerManager.skipToNext()
    }

    override suspend fun skipToPrevious() {
        controllerManager.skipToPrevious()
    }

    override suspend fun toggleShuffle() {
        controllerManager.toggleShuffle()
    }

    override suspend fun setRepeatMode(mode: RepeatMode) {
        controllerManager.setRepeatMode(mode)
    }

    override suspend fun reorderQueue(fromIndex: Int, toIndex: Int) {
        // Queue reordering delegate
    }
}
