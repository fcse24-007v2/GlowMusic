package com.example.glowmusic

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.glowmusic.di.AppContainer
import com.example.glowmusic.ui.components.MiniPlayerBar
import com.example.glowmusic.ui.screens.HomeScreen
import com.example.glowmusic.ui.screens.LibraryScreen
import com.example.glowmusic.ui.screens.PlayerScreen
import com.example.glowmusic.ui.screens.SettingsScreen
import com.example.glowmusic.ui.theme.GlowMusicTheme
import com.example.glowmusic.ui.viewmodel.HomeViewModel
import com.example.glowmusic.ui.viewmodel.LibraryViewModel
import com.example.glowmusic.ui.viewmodel.PlayerViewModel
import com.example.glowmusic.ui.viewmodel.SearchViewModel
import com.example.glowmusic.ui.viewmodel.ThemeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var container: AppContainer
    private var hasStartedInitialMediaScan = false

    private val mediaPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grantResults ->
            val anyDenied = grantResults.any { !it.value }
            if (anyDenied) {
                Toast.makeText(
                    this,
                    "Media permission denied. Local files won't appear until you allow it.",
                    Toast.LENGTH_LONG
                ).show()
            }
            maybeStartMediaScan()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Access DI Container
        container = (application as GlowMusicApplication).container

        requestMediaPermissionsIfNeeded()
        maybeStartMediaScan()

        // Initialize ViewModels with injected dependencies
        val homeViewModel = HomeViewModel(
            container.getMediaLibraryUseCase,
            container.toggleFavoriteUseCase
        )
        val libraryViewModel = LibraryViewModel(container.getMediaLibraryUseCase, container.getPlaylistsUseCase)
        val searchViewModel = SearchViewModel(container.searchMediaUseCase)
        val themeViewModel = ThemeViewModel(
            container.getThemeSettingsUseCase,
            container.setThemeStyleUseCase
        )
        val playerViewModel = PlayerViewModel(
            container.playbackRepository,
            container.getQueueUseCase
        )

        setContent {
            val themeUiState by themeViewModel.uiState.collectAsState()
            val homeUiState by homeViewModel.uiState.collectAsState()
            val libraryUiState by libraryViewModel.uiState.collectAsState()
            val playerUiState by playerViewModel.uiState.collectAsState()
            val searchUiState by searchViewModel.uiState.collectAsState()

            var selectedNavIndex by remember { mutableIntStateOf(0) }
            var isPlayerExpanded by remember { mutableStateOf(false) }

            GlowMusicTheme(themeSettings = themeUiState.themeSettings) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = MaterialTheme.colorScheme.background,
                        bottomBar = {
                            if (!isPlayerExpanded) {
                                NavigationBar(
                                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                                    contentColor = MaterialTheme.colorScheme.primary
                                ) {
                                    NavigationBarItem(
                                        selected = selectedNavIndex == 0,
                                        onClick = { selectedNavIndex = 0 },
                                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                                        label = { Text("Home") },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = MaterialTheme.colorScheme.primary,
                                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                        )
                                    )
                                    NavigationBarItem(
                                        selected = selectedNavIndex == 1,
                                        onClick = { selectedNavIndex = 1 },
                                        icon = { Icon(Icons.Default.LibraryMusic, contentDescription = "Library") },
                                        label = { Text("Library") },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = MaterialTheme.colorScheme.primary,
                                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                        )
                                    )
                                    NavigationBarItem(
                                        selected = selectedNavIndex == 2,
                                        onClick = { selectedNavIndex = 2 },
                                        icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                                        label = { Text("Settings") },
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = MaterialTheme.colorScheme.primary,
                                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                                        )
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            when (selectedNavIndex) {
                                0 -> HomeScreen(
                                    uiState = homeUiState,
                                    onMediaItemClick = { item ->
                                        playerViewModel.playMedia(item, homeUiState.recentAudio)
                                    },
                                    onToggleFavorite = { homeViewModel.toggleFavorite(it) }
                                )

                                1 -> LibraryScreen(
                                    uiState = libraryUiState,
                                    onTabSelected = { libraryViewModel.setSelectedTab(it) },
                                    onMediaItemClick = { item ->
                                        playerViewModel.playMedia(item, libraryUiState.audioItems)
                                    },
                                    getPlaylistTracks = { id -> libraryViewModel.getPlaylistTracksFlow(id) },
                                    searchUiState = searchUiState,
                                    onSearchQueryChanged = { searchViewModel.onSearchQueryChanged(it) }
                                )

                                2 -> SettingsScreen(
                                    themeUiState = themeUiState,
                                    onStyleSelected = { themeViewModel.setThemeStyle(it) },
                                    onDarkModeSelected = { themeViewModel.setDarkModePreference(it) }
                                )
                            }

                            // Persistent Mini Player Floating Bar
                            if (playerUiState.playbackState.currentItem != null && !isPlayerExpanded) {
                                val current = playerUiState.playbackState.currentItem
                                val isPlaying = playerUiState.playbackState.isPlaying
                                val pos = playerUiState.playbackState.currentPositionMs.toFloat()
                                val dur =
                                    if (playerUiState.playbackState.totalDurationMs > 0) playerUiState.playbackState.totalDurationMs.toFloat() else 1f

                                MiniPlayerBar(
                                    currentMedia = current,
                                    isPlaying = isPlaying,
                                    progressFraction = pos / dur,
                                    onPlayPauseClick = { playerViewModel.togglePlayPause() },
                                    onSkipNextClick = { playerViewModel.skipToNext() },
                                    onBarClick = { isPlayerExpanded = true },
                                    modifier = Modifier.align(Alignment.BottomCenter)
                                )
                            }
                        }
                    }

                    // Full Player Overlay Screen
                    AnimatedVisibility(
                        visible = isPlayerExpanded,
                        enter = slideInVertically(initialOffsetY = { it }),
                        exit = slideOutVertically(targetOffsetY = { it })
                    ) {
                        PlayerScreen(
                            uiState = playerUiState,
                            onCollapseClick = { isPlayerExpanded = false },
                            onPlayPauseClick = { playerViewModel.togglePlayPause() },
                            onSeekTo = { playerViewModel.seekTo(it) },
                            onSkipNext = { playerViewModel.skipToNext() },
                            onSkipPrevious = { playerViewModel.skipToPrevious() },
                            onToggleShuffle = { playerViewModel.toggleShuffle() }
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        maybeStartMediaScan()
    }

    private fun requestMediaPermissionsIfNeeded() {
        val missingPermissions = missingRequiredMediaPermissions()
        if (missingPermissions.isNotEmpty()) {
            mediaPermissionsLauncher.launch(missingPermissions.toTypedArray())
        }
    }

    private fun maybeStartMediaScan() {
        if (!::container.isInitialized) return
        if (hasStartedInitialMediaScan) return
        if (!hasRequiredMediaPermissions()) return

        hasStartedInitialMediaScan = true
        CoroutineScope(Dispatchers.IO).launch {
            runCatching { container.mediaRepository.scanMediaStore() }
                .onFailure { throwable ->
                    hasStartedInitialMediaScan = false
                    Log.e("MainActivity", "Media scan failed", throwable)
                }
        }
    }

    private fun hasRequiredMediaPermissions(): Boolean = missingRequiredMediaPermissions().isEmpty()

    private fun missingRequiredMediaPermissions(): List<String> {
        val missing = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!isPermissionGranted(Manifest.permission.READ_MEDIA_AUDIO)) {
                missing += Manifest.permission.READ_MEDIA_AUDIO
            }
        } else {
            if (!isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                missing += Manifest.permission.READ_EXTERNAL_STORAGE
            }
        }

        return missing
    }

    private fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }
}
