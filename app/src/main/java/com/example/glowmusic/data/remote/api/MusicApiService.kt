package com.example.glowmusic.data.remote.api

import com.example.glowmusic.data.remote.dto.PlaylistDto
import com.example.glowmusic.data.remote.dto.TrackDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MusicApiService {
    @GET("tracks")
    suspend fun getTracks(
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): List<TrackDto>

    @GET("tracks/search")
    suspend fun searchTracks(
        @Query("q") query: String,
        @Query("limit") limit: Int = 20
    ): List<TrackDto>

    @GET("tracks/{id}")
    suspend fun getTrack(@Path("id") id: String): TrackDto

    @GET("playlists")
    suspend fun getPlaylists(
        @Query("limit") limit: Int = 50
    ): List<PlaylistDto>

    @GET("playlists/{id}")
    suspend fun getPlaylist(@Path("id") id: String): PlaylistDto
}
