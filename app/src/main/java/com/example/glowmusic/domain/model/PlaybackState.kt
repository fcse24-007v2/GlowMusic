package com.example.glowmusic.domain.model

enum class RepeatMode {
    OFF,
    ONE,
    ALL
}

data class PlaybackState(
    val currentItem: MediaItem? = null,
    val isPlaying: Boolean = false,
    val currentPositionMs: Long = 0L,
    val totalDurationMs: Long = 0L,
    val shuffleModeEnabled: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.OFF,
    val playbackSpeed: Float = 1.0f,
    val isBuffering: Boolean = false
)
