package com.example.glowmusic.data.remote

import android.util.Log
import com.example.glowmusic.data.remote.dto.PlaylistDto
import com.example.glowmusic.data.remote.dto.TrackDto

class MusicNetworkService(
    private val apiService: com.example.glowmusic.data.remote.api.MusicApiService
) {
    suspend fun fetchTracks(limit: Int = 50, offset: Int = 0): Result<List<TrackDto>> {
        return try {
            val tracks = apiService.getTracks(limit, offset)
            Log.d("MusicNetworkService", "Fetched ${tracks.size} tracks")
            Result.success(tracks)
        } catch (e: Exception) {
            Log.e("MusicNetworkService", "Failed to fetch tracks: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun searchTracks(query: String, limit: Int = 20): Result<List<TrackDto>> {
        return try {
            val tracks = apiService.searchTracks(query, limit)
            Log.d("MusicNetworkService", "Search '$query' returned ${tracks.size} tracks")
            Result.success(tracks)
        } catch (e: Exception) {
            Log.e("MusicNetworkService", "Failed to search tracks: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun getTrack(id: String): Result<TrackDto> {
        return try {
            val track = apiService.getTrack(id)
            Log.d("MusicNetworkService", "Fetched track: ${track.title}")
            Result.success(track)
        } catch (e: Exception) {
            Log.e("MusicNetworkService", "Failed to fetch track $id: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun fetchPlaylists(limit: Int = 50): Result<List<PlaylistDto>> {
        return try {
            val playlists = apiService.getPlaylists(limit)
            Log.d("MusicNetworkService", "Fetched ${playlists.size} playlists")
            Result.success(playlists)
        } catch (e: Exception) {
            Log.e("MusicNetworkService", "Failed to fetch playlists: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun getPlaylist(id: String): Result<PlaylistDto> {
        return try {
            val playlist = apiService.getPlaylist(id)
            Log.d("MusicNetworkService", "Fetched playlist: ${playlist.name}")
            Result.success(playlist)
        } catch (e: Exception) {
            Log.e("MusicNetworkService", "Failed to fetch playlist $id: ${e.message}", e)
            Result.failure(e)
        }
    }
}
