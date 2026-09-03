package com.example.glowmusic.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlaylistDto(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("description")
    val description: String? = null,
    @SerialName("cover_url")
    val coverUrl: String? = null,
    @SerialName("tracks")
    val tracks: List<TrackDto> = emptyList(),
    @SerialName("track_count")
    val trackCount: Int = 0
)
