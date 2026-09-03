package com.example.glowmusic.domain.model

data class LyricWord(
    val text: String,
    val timeMs: Long
)

data class LyricLine(
    val timeMs: Long,
    val text: String,
    val words: List<LyricWord> = emptyList()
)
