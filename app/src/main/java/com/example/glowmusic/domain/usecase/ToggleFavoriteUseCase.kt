package com.example.glowmusic.domain.usecase

import com.example.glowmusic.domain.repository.MediaRepository

class ToggleFavoriteUseCase(
    private val mediaRepository: MediaRepository
) {
    suspend operator fun invoke(mediaId: Long) {
        mediaRepository.toggleFavorite(mediaId)
    }
}
