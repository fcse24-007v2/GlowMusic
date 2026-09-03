package com.example.glowmusic.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TrackDto(
    @SerialName("id")
    val id: String,
    @SerialName("title")
    val title: String,
    @SerialName("artist")
    val artist: String,
    @SerialName("album")
    val album: String,
    @SerialName("duration")
    val durationMs: Long,
    @SerialName("url")
    val url: String,
    @SerialName("artwork_url")
    val artworkUrl: String? = null,
    @SerialName("genre")
    val genre: String? = null
)
