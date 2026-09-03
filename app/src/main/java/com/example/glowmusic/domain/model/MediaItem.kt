package com.example.glowmusic.domain.model

enum class MediaType {
    AUDIO
}

data class MediaItem(
    val id: Long,
    val contentUri: String,
    val title: String,
    val artist: String = "Unknown Artist",
    val album: String = "Unknown Album",
    val durationMs: Long = 0L,
    val artworkUri: String? = null,
    val embeddedLyrics: String? = null,
    val mediaType: MediaType = MediaType.AUDIO,
    val trackNumber: Int = 0,
    val year: Int = 0,
    val sizeBytes: Long = 0L,
    val isFavorite: Boolean = false
)
