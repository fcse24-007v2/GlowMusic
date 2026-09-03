package com.example.glowmusic.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.glowmusic.domain.model.Playlist

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val createdAt: Long = System.currentTimeMillis(),
    val coverUri: String? = null
) {
    fun toDomain(trackCount: Int): Playlist = Playlist(
        id = id,
        name = name,
        createdAt = createdAt,
        trackCount = trackCount,
        coverUri = coverUri
    )
}

@Entity(tableName = "playlist_tracks", primaryKeys = ["playlistId", "trackId"])
data class PlaylistTrackCrossRef(
    val playlistId: Long,
    val trackId: Long,
    val addedAt: Long = System.currentTimeMillis()
)
