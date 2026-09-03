package com.example.glowmusic.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.glowmusic.domain.model.MediaItem
import com.example.glowmusic.domain.model.MediaType

@Entity(tableName = "tracks")
data class TrackEntity(
    @PrimaryKey val id: Long,
    val contentUri: String,
    val title: String,
    val artist: String,
    val album: String,
    val durationMs: Long,
    val artworkUri: String?,
    val embeddedLyrics: String? = null,
    val mediaType: String = MediaType.AUDIO.name,
    val trackNumber: Int,
    val year: Int,
    val sizeBytes: Long,
    val isFavorite: Boolean = false
) {
    fun toDomain(): MediaItem = MediaItem(
        id = id,
        contentUri = contentUri,
        title = title,
        artist = artist,
        album = album,
        durationMs = durationMs,
        artworkUri = artworkUri,
        embeddedLyrics = embeddedLyrics,
        mediaType = MediaType.AUDIO,
        trackNumber = trackNumber,
        year = year,
        sizeBytes = sizeBytes,
        isFavorite = isFavorite
    )

    companion object {
        fun fromDomain(item: MediaItem): TrackEntity = TrackEntity(
            id = item.id,
            contentUri = item.contentUri,
            title = item.title,
            artist = item.artist,
            album = item.album,
            durationMs = item.durationMs,
            artworkUri = item.artworkUri,
            embeddedLyrics = item.embeddedLyrics,
            mediaType = MediaType.AUDIO.name,
            trackNumber = item.trackNumber,
            year = item.year,
            sizeBytes = item.sizeBytes,
            isFavorite = item.isFavorite
        )
    }
}
