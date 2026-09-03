package com.example.glowmusic.domain.usecase

import com.example.glowmusic.domain.model.MediaItem
import com.example.glowmusic.domain.repository.PlaybackRepository
import kotlinx.coroutines.flow.Flow

class GetQueueUseCase(
    private val playbackRepository: PlaybackRepository
) {
    operator fun invoke(): Flow<List<MediaItem>> {
        return playbackRepository.getQueue()
    }
}
