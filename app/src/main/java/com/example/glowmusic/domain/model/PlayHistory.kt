package com.example.glowmusic.domain.model

data class PlayHistory(
    val id: Long = 0,
    val mediaId: Long,
    val title: String,
    val artist: String,
    val artworkUri: String?,
    val playedAt: Long = System.currentTimeMillis()
)
