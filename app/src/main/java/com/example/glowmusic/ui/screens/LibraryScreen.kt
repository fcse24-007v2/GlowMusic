package com.example.glowmusic.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.glowmusic.domain.model.MediaItem
import com.example.glowmusic.domain.model.Playlist
import com.example.glowmusic.ui.components.GlassCard
import com.example.glowmusic.ui.theme.Dimens
import com.example.glowmusic.ui.uistate.LibraryUiState
import com.example.glowmusic.ui.uistate.SearchUiState

@Composable
fun LibraryScreen(
    uiState: LibraryUiState,
    onTabSelected: (Int) -> Unit,
    onMediaItemClick: (MediaItem) -> Unit,
    getPlaylistTracks: (Long) -> kotlinx.coroutines.flow.Flow<List<MediaItem>>,
    searchUiState: SearchUiState,
    onSearchQueryChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Text(
            text = "Library",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = Dimens.spacingSm, vertical = Dimens.spacingSm)
        )

        TextField(
            value = searchUiState.searchQuery,
            onValueChange = { onSearchQueryChanged(it) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            placeholder = { Text("Search music or playlists") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.spacingSm, vertical = Dimens.spacingXs)
        )

        TabRow(
            selectedTabIndex = uiState.selectedTab,
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Tab(
                selected = uiState.selectedTab == 0,
                onClick = { onTabSelected(0) },
                text = { Text("Music (${uiState.audioItems.size})") },
                icon = { Icon(Icons.Default.MusicNote, contentDescription = null) }
            )
            Tab(
                selected = uiState.selectedTab == 1,
                onClick = { onTabSelected(1) },
                text = { Text("Playlists") },
                icon = { Icon(Icons.AutoMirrored.Filled.PlaylistPlay, contentDescription = null) }
            )
        }

        if (searchUiState.searchQuery.isNotBlank()) {
            if (searchUiState.isSearching) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 100.dp, top = Dimens.spacingSm),
                    verticalArrangement = Arrangement.spacedBy(Dimens.spacingXs)
                ) {
                    items(searchUiState.searchResults, key = { it.id }) { item ->
                        MediaSearchRow(item = item, onMediaItemClick = onMediaItemClick)
                    }
                }
            }
        } else if (uiState.selectedTab == 1) {
            PlaylistsList(
                playlists = uiState.playlists,
                onPlaylistClick = { },
                onMediaItemClick = onMediaItemClick,
                getPlaylistTracks = getPlaylistTracks
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 100.dp, top = Dimens.spacingSm),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacingXs)
            ) {
                items(uiState.audioItems, key = { it.id }) { item ->
                    MediaSearchRow(item = item, onMediaItemClick = onMediaItemClick)
                }
            }
        }
    }
}

@Composable
private fun MediaSearchRow(
    item: MediaItem,
    onMediaItemClick: (MediaItem) -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.spacingSm)
            .clickable { onMediaItemClick(item) },
        shape = RoundedCornerShape(Dimens.cornerRadiusSmallCard)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                if (!item.artworkUri.isNullOrEmpty()) {
                    AsyncImage(
                        model = item.artworkUri,
                        contentDescription = item.title,
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.width(Dimens.spacingSm))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${item.artist} • ${item.album}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun PlaylistsList(
    playlists: List<Playlist>,
    onPlaylistClick: (Playlist) -> Unit,
    onMediaItemClick: (MediaItem) -> Unit,
    getPlaylistTracks: (Long) -> kotlinx.coroutines.flow.Flow<List<MediaItem>>
) {
    var selectedPlaylistId by remember { mutableStateOf<Long?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 100.dp, top = Dimens.spacingSm),
        verticalArrangement = Arrangement.spacedBy(Dimens.spacingXs)
    ) {
        items(playlists, key = { it.id }) { playlist ->
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.spacingSm)
                    .clickable {
                        selectedPlaylistId = playlist.id
                        onPlaylistClick(playlist)
                    },
                shape = RoundedCornerShape(Dimens.cornerRadiusSmallCard)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.PlaylistPlay,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.width(Dimens.spacingSm))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = playlist.name,
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${playlist.trackCount} tracks",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    if (selectedPlaylistId != null) {
        val tracksState by getPlaylistTracks(selectedPlaylistId!!).collectAsState(initial = emptyList())
        PlaylistDetail(
            tracks = tracksState,
            onClose = { selectedPlaylistId = null },
            onMediaItemClick = onMediaItemClick
        )
    }
}

@Composable
private fun PlaylistDetail(
    tracks: List<MediaItem>,
    onClose: () -> Unit,
    onMediaItemClick: (MediaItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(Dimens.spacingSm)
    ) {
        Text(
            text = "Playlist",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.clickable { onClose() }
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = Dimens.spacingSm, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacingXs)
        ) {
            items(tracks, key = { it.id }) { item ->
                MediaSearchRow(item = item, onMediaItemClick = onMediaItemClick)
            }
        }
    }
}
