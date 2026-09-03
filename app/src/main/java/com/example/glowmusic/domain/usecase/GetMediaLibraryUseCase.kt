package com.example.glowmusic.domain.usecase

import com.example.glowmusic.domain.model.MediaItem
import com.example.glowmusic.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow

class GetMediaLibraryUseCase(
    private val mediaRepository: MediaRepository
) {
    operator fun invoke(): Flow<List<MediaItem>> {
        return mediaRepository.getAllMedia()
    }

    fun getAudio(): Flow<List<MediaItem>> = mediaRepository.getAudioMedia()
}
