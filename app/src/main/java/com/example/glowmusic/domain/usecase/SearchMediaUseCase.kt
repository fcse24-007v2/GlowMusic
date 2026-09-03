package com.example.glowmusic.domain.usecase

import com.example.glowmusic.domain.model.MediaItem
import com.example.glowmusic.domain.repository.MediaRepository
import kotlinx.coroutines.flow.Flow

class SearchMediaUseCase(
    private val mediaRepository: MediaRepository
) {
    operator fun invoke(query: String): Flow<List<MediaItem>> {
        return mediaRepository.searchMedia(query)
    }
}
