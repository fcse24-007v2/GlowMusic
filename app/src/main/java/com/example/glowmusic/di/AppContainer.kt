package com.example.glowmusic.di

import android.content.Context
import android.util.Log
import coil.ImageLoader
import com.example.glowmusic.data.image.ImageLoaderFactory
import com.example.glowmusic.data.local.datastore.ThemePreferences
import com.example.glowmusic.data.local.db.AppDatabase
import com.example.glowmusic.data.local.scanner.MediaStoreScanner
import com.example.glowmusic.data.repository.InMemoryMediaRepository
import com.example.glowmusic.data.repository.InMemoryPlaylistRepository
import com.example.glowmusic.data.repository.MediaRepositoryImpl
import com.example.glowmusic.data.repository.PlaybackRepositoryImpl
import com.example.glowmusic.data.repository.PlaylistRepositoryImpl
import com.example.glowmusic.data.repository.ThemeRepositoryImpl
import com.example.glowmusic.domain.repository.MediaRepository
import com.example.glowmusic.domain.repository.PlaybackRepository
import com.example.glowmusic.domain.repository.PlaylistRepository
import com.example.glowmusic.domain.repository.ThemeRepository
import com.example.glowmusic.domain.usecase.GetMediaLibraryUseCase
import com.example.glowmusic.domain.usecase.GetQueueUseCase
import com.example.glowmusic.domain.usecase.GetThemeSettingsUseCase
import com.example.glowmusic.domain.usecase.ResolveNowPlayingColorsUseCase
import com.example.glowmusic.domain.usecase.SearchMediaUseCase
import com.example.glowmusic.domain.usecase.GetPlaylistsUseCase
import com.example.glowmusic.domain.usecase.SetThemeStyleUseCase
import com.example.glowmusic.domain.usecase.ToggleFavoriteUseCase
import com.example.glowmusic.playback.GlowMediaControllerManager

interface AppContainer {
    val mediaRepository: MediaRepository
    val playlistRepository: PlaylistRepository
    val themeRepository: ThemeRepository
    val playbackRepository: PlaybackRepository
    val controllerManager: GlowMediaControllerManager
    val imageLoader: ImageLoader

    val getMediaLibraryUseCase: GetMediaLibraryUseCase
    val getPlaylistsUseCase: GetPlaylistsUseCase
    val toggleFavoriteUseCase: ToggleFavoriteUseCase
    val getThemeSettingsUseCase: GetThemeSettingsUseCase
    val setThemeStyleUseCase: SetThemeStyleUseCase
    val getQueueUseCase: GetQueueUseCase
    val resolveNowPlayingColorsUseCase: ResolveNowPlayingColorsUseCase
    val searchMediaUseCase: SearchMediaUseCase
}

class DefaultAppContainer(private val context: Context) : AppContainer {

    private val database: AppDatabase by lazy {
        AppDatabase.getInstance(context)
    }

    private val mediaStoreScanner: MediaStoreScanner by lazy {
        MediaStoreScanner(context)
    }

    private val themePreferences: ThemePreferences by lazy {
        ThemePreferences(context)
    }

    override val controllerManager: GlowMediaControllerManager by lazy {
        GlowMediaControllerManager(context)
    }

    override val imageLoader: ImageLoader by lazy {
        ImageLoaderFactory.createImageLoader(context)
    }

    override val mediaRepository: MediaRepository by lazy {
        runCatching {
            MediaRepositoryImpl(database.trackDao(), mediaStoreScanner)
        }.getOrElse { throwable ->
            Log.e("DefaultAppContainer", "Falling back to in-memory media repository", throwable)
            InMemoryMediaRepository(mediaStoreScanner)
        }
    }

    override val playlistRepository: PlaylistRepository by lazy {
        runCatching {
            PlaylistRepositoryImpl(database.playlistDao())
        }.getOrElse { throwable ->
            Log.e("DefaultAppContainer", "Falling back to in-memory playlist repository", throwable)
            InMemoryPlaylistRepository(mediaRepository)
        }
    }

    override val themeRepository: ThemeRepository by lazy {
        ThemeRepositoryImpl(themePreferences)
    }

    override val playbackRepository: PlaybackRepository by lazy {
        PlaybackRepositoryImpl(controllerManager)
    }

    override val getMediaLibraryUseCase: GetMediaLibraryUseCase by lazy {
        GetMediaLibraryUseCase(mediaRepository)
    }

    override val getPlaylistsUseCase: GetPlaylistsUseCase by lazy {
        GetPlaylistsUseCase(playlistRepository)
    }

    override val toggleFavoriteUseCase: ToggleFavoriteUseCase by lazy {
        ToggleFavoriteUseCase(mediaRepository)
    }

    override val getThemeSettingsUseCase: GetThemeSettingsUseCase by lazy {
        GetThemeSettingsUseCase(themeRepository)
    }

    override val setThemeStyleUseCase: SetThemeStyleUseCase by lazy {
        SetThemeStyleUseCase(themeRepository)
    }

    override val getQueueUseCase: GetQueueUseCase by lazy {
        GetQueueUseCase(playbackRepository)
    }

    override val resolveNowPlayingColorsUseCase: ResolveNowPlayingColorsUseCase by lazy {
        ResolveNowPlayingColorsUseCase()
    }

    override val searchMediaUseCase: SearchMediaUseCase by lazy {
        SearchMediaUseCase(mediaRepository)
    }
}
