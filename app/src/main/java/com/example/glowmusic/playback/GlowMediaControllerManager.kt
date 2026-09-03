package com.example.glowmusic.playback

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem as Media3Item
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.glowmusic.domain.model.MediaItem
import com.example.glowmusic.domain.model.PlaybackState
import com.example.glowmusic.domain.model.RepeatMode
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class GlowMediaControllerManager(
    private val context: Context,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main + Job())
) {
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var mediaController: MediaController? = null

    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    private val _queue = MutableStateFlow<List<MediaItem>>(emptyList())
    val queue: StateFlow<List<MediaItem>> = _queue.asStateFlow()

    private var progressTickerJob: Job? = null
    private val mediaItemMap = mutableMapOf<String, MediaItem>()

    private var pendingPlayItem: MediaItem? = null
    private var pendingQueue: List<MediaItem> = emptyList()

    init {
        initializeController()
    }

    private fun initializeController() {
        val sessionToken = SessionToken(context, ComponentName(context, GlowMediaSessionService::class.java))
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()

        controllerFuture?.addListener({
            try {
                mediaController = controllerFuture?.get()
                setupPlayerListener()
                syncStateFromController()
                flushPendingPlayRequestIfAny()
            } catch (_: Exception) {
                // Keep state fallback active.
            }
        }, MoreExecutors.directExecutor())
    }

    private fun setupPlayerListener() {
        mediaController?.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                updatePlaybackState()
                if (isPlaying) {
                    startProgressTicker()
                } else {
                    stopProgressTicker()
                }
            }

            override fun onPlaybackStateChanged(state: Int) {
                updatePlaybackState()
            }

            override fun onMediaItemTransition(item: Media3Item?, reason: Int) {
                updatePlaybackState()
            }

            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) {
                updatePlaybackState()
            }
        })
    }

    private fun startProgressTicker() {
        progressTickerJob?.cancel()
        progressTickerJob = scope.launch {
            while (isActive) {
                mediaController?.let { controller ->
                    _playbackState.update {
                        it.copy(
                            currentPositionMs = controller.currentPosition.coerceAtLeast(0L),
                            totalDurationMs = controller.duration.coerceAtLeast(0L)
                        )
                    }
                }
                delay(400L)
            }
        }
    }

    private fun stopProgressTicker() {
        progressTickerJob?.cancel()
        progressTickerJob = null
    }

    private fun updatePlaybackState() {
        val controller = mediaController ?: return
        val currentMediaId = controller.currentMediaItem?.mediaId
        val domainItem = mediaItemMap[currentMediaId]

        val isPlaying = controller.isPlaying
        val isBuffering = controller.playbackState == Player.STATE_BUFFERING
        val currentPos = controller.currentPosition.coerceAtLeast(0L)
        val duration = controller.duration.coerceAtLeast(0L)

        val repeatMode = when (controller.repeatMode) {
            Player.REPEAT_MODE_ONE -> RepeatMode.ONE
            Player.REPEAT_MODE_ALL -> RepeatMode.ALL
            else -> RepeatMode.OFF
        }

        _playbackState.update {
            it.copy(
                currentItem = domainItem ?: it.currentItem,
                isPlaying = isPlaying,
                isBuffering = isBuffering,
                currentPositionMs = currentPos,
                totalDurationMs = if (duration > 0) duration else (domainItem?.durationMs ?: 0L),
                shuffleModeEnabled = controller.shuffleModeEnabled,
                repeatMode = repeatMode
            )
        }
    }

    fun playMedia(item: MediaItem, queueList: List<MediaItem>) {
        val itemsToPlay = if (queueList.isNotEmpty()) queueList else listOf(item)
        _queue.value = itemsToPlay

        val controller = mediaController ?: run {
            pendingPlayItem = item
            pendingQueue = itemsToPlay
            _playbackState.update {
                it.copy(
                    currentItem = item,
                    isPlaying = true,
                    currentPositionMs = 0L,
                    totalDurationMs = item.durationMs
                )
            }
            return
        }

        mediaItemMap.clear()
        val media3Items = itemsToPlay.map { domainMedia ->
            mediaItemMap[domainMedia.id.toString()] = domainMedia
            buildMedia3Item(domainMedia)
        }

        val targetIndex = itemsToPlay.indexOfFirst { it.id == item.id }.coerceAtLeast(0)

        controller.setMediaItems(media3Items, targetIndex, 0L)
        controller.prepare()
        controller.playWhenReady = true
        controller.play()

        _playbackState.update {
            it.copy(
                currentItem = item,
                isPlaying = true,
                currentPositionMs = 0L,
                totalDurationMs = item.durationMs
            )
        }
    }

    fun pause() {
        mediaController?.pause()
        _playbackState.update { it.copy(isPlaying = false) }
    }

    fun resume() {
        mediaController?.playWhenReady = true
        mediaController?.play()
        _playbackState.update { it.copy(isPlaying = true) }
    }

    fun seekTo(positionMs: Long) {
        mediaController?.seekTo(positionMs)
        _playbackState.update { it.copy(currentPositionMs = positionMs) }
    }

    fun skipToNext() {
        val queue = _queue.value
        if (queue.isEmpty()) return

        val currentId = _playbackState.value.currentItem?.id
        val currentIndex = queue.indexOfFirst { it.id == currentId }.let { if (it >= 0) it else 0 }
        val nextIndex = when {
            currentIndex + 1 < queue.size -> currentIndex + 1
            _playbackState.value.repeatMode == RepeatMode.ALL -> 0
            else -> currentIndex
        }

        if (nextIndex == currentIndex) {
            mediaController?.seekTo(0L)
            mediaController?.playWhenReady = true
            mediaController?.play()
            return
        }

        playMedia(queue[nextIndex], queue)
    }

    fun skipToPrevious() {
        val queue = _queue.value
        if (queue.isEmpty()) return

        val currentId = _playbackState.value.currentItem?.id
        val currentIndex = queue.indexOfFirst { it.id == currentId }.let { if (it >= 0) it else 0 }
        val previousIndex = when {
            currentIndex > 0 -> currentIndex - 1
            _playbackState.value.repeatMode == RepeatMode.ALL -> queue.lastIndex
            else -> currentIndex
        }

        if (previousIndex == currentIndex) {
            mediaController?.seekTo(0L)
            mediaController?.playWhenReady = true
            mediaController?.play()
            return
        }

        playMedia(queue[previousIndex], queue)
    }

    fun toggleShuffle() {
        val controller = mediaController
        if (controller != null) {
            val newShuffle = !controller.shuffleModeEnabled
            controller.shuffleModeEnabled = newShuffle
            _playbackState.update { it.copy(shuffleModeEnabled = newShuffle) }
        } else {
            _playbackState.update { it.copy(shuffleModeEnabled = !it.shuffleModeEnabled) }
        }
    }

    fun setRepeatMode(mode: RepeatMode) {
        val controller = mediaController
        val media3Repeat = when (mode) {
            RepeatMode.ONE -> Player.REPEAT_MODE_ONE
            RepeatMode.ALL -> Player.REPEAT_MODE_ALL
            RepeatMode.OFF -> Player.REPEAT_MODE_OFF
        }
        controller?.repeatMode = media3Repeat
        _playbackState.update { it.copy(repeatMode = mode) }
    }

    private fun buildMedia3Item(domainMedia: MediaItem): Media3Item {
        val metadata = MediaMetadata.Builder()
            .setTitle(domainMedia.title)
            .setArtist(domainMedia.artist)
            .setAlbumTitle(domainMedia.album)
            .setArtworkUri(domainMedia.artworkUri?.let { Uri.parse(it) })
            .build()

        return Media3Item.Builder()
            .setMediaId(domainMedia.id.toString())
            .setUri(Uri.parse(domainMedia.contentUri))
            .setMediaMetadata(metadata)
            .build()
    }

    private fun syncStateFromController() {
        updatePlaybackState()
    }

    private fun flushPendingPlayRequestIfAny() {
        val item = pendingPlayItem ?: return
        val queue = pendingQueue
        pendingPlayItem = null
        pendingQueue = emptyList()
        playMedia(item, queue)
    }

    fun release() {
        stopProgressTicker()
        controllerFuture?.let { MediaController.releaseFuture(it) }
        mediaController = null
    }
}
