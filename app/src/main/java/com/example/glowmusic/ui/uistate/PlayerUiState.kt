package com.example.glowmusic.ui.uistate

import com.example.glowmusic.domain.model.ColorPalette
import com.example.glowmusic.domain.model.LyricLine
import com.example.glowmusic.domain.model.MediaItem
import com.example.glowmusic.domain.model.PlaybackState

data class PlayerUiState(
    val playbackState: PlaybackState = PlaybackState(),
    val queue: List<MediaItem> = emptyList(),
    val colorPalette: ColorPalette = ColorPalette(),
    val lyrics: List<LyricLine> = emptyList(),
    val currentLyricLineIndex: Int = -1,
    val currentLyricWordIndex: Int = -1,
    val isExpanded: Boolean = false,
    val isQueueSheetOpen: Boolean = false
)
