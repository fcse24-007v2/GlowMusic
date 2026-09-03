package com.example.glowmusic.playback

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.os.Build
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.ui.PlayerNotificationManager
import com.example.glowmusic.MainActivity

@OptIn(UnstableApi::class)
class GlowMediaSessionService : MediaSessionService() {

    private var mediaSession: MediaSession? = null
    private var notificationManager: PlayerNotificationManager? = null
    private val audioManager by lazy { getSystemService(Context.AUDIO_SERVICE) as AudioManager }
    private val headsetReceiver = HeadsetPlugReceiver()

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()

        val player = ExoPlayer.Builder(this)
            .setAudioAttributes(audioAttributes, /* handleAudioFocus = */ true)
            .setHandleAudioBecomingNoisy(true)
            .build()

        val sessionActivityIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            sessionActivityIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        mediaSession = MediaSession.Builder(this, player)
            .setId("GlowMusicMediaSession")
            .setSessionActivity(pendingIntent)
            .build()

        // Setup notification manager
        setupNotificationManager(player)

        // Register headset unplug receiver
        registerHeadsetReceiver()

        // Setup audio focus listener
        player.addListener(object : androidx.media3.common.Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (!isPlaying) {
                    // Check why playback stopped
                    val focusLoss = audioManager.requestAudioFocus(
                        { focusChange ->
                            if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                                player.pause()
                            }
                        },
                        AudioManager.STREAM_MUSIC,
                        AudioManager.AUDIOFOCUS_GAIN
                    )
                }
            }
        })
    }

    @OptIn(UnstableApi::class)
    private fun setupNotificationManager(player: ExoPlayer) {
        notificationManager = PlayerNotificationManager.Builder(this, NOTIFICATION_ID, NOTIFICATION_CHANNEL_ID)
            .setChannelNameResourceId(com.example.glowmusic.R.string.app_name)
            .setChannelDescriptionResourceId(com.example.glowmusic.R.string.app_name)
            .setMediaDescriptionAdapter(MediaDescriptionAdapter())
            .setSmallIconResourceId(com.example.glowmusic.R.drawable.ic_launcher_foreground)
            .build()
            .apply {
                setPlayer(player)
                setUseChronometer(true)
            }
    }

    private fun registerHeadsetReceiver() {
        val intentFilter = IntentFilter(AudioManager.ACTION_HEADSET_PLUG)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            registerReceiver(headsetReceiver, intentFilter, Context.RECEIVER_EXPORTED)
        } else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            registerReceiver(headsetReceiver, intentFilter)
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        notificationManager?.setPlayer(null)
        unregisterReceiver(headsetReceiver)
        super.onDestroy()
    }

    private inner class HeadsetPlugReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == AudioManager.ACTION_HEADSET_PLUG) {
                val state = intent.getIntExtra("state", -1)
                val player = mediaSession?.player ?: return

                when (state) {
                    0 -> {
                        // Headset unplugged
                        if (player.isPlaying) {
                            player.pause()
                        }
                    }
                    1 -> {
                        // Headset plugged in
                        // Resume playback automatically (optional)
                    }
                }
            }
        }
    }

    private inner class MediaDescriptionAdapter : PlayerNotificationManager.MediaDescriptionAdapter {
        override fun getCurrentContentTitle(player: androidx.media3.common.Player): CharSequence {
            return player.mediaMetadata.title ?: "GlowMusic"
        }

        override fun createCurrentContentIntent(player: androidx.media3.common.Player): PendingIntent? {
            val intent = Intent(this@GlowMediaSessionService, MainActivity::class.java)
            return PendingIntent.getActivity(
                this@GlowMediaSessionService,
                0,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        override fun getCurrentContentText(player: androidx.media3.common.Player): CharSequence? {
            return player.mediaMetadata.artist ?: "Unknown Artist"
        }

        override fun getCurrentLargeIcon(
            player: androidx.media3.common.Player,
            callback: PlayerNotificationManager.BitmapCallback
        ): android.graphics.Bitmap? {
            return null // Coil will handle image loading later
        }
    }

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val NOTIFICATION_CHANNEL_ID = "glowmusic_playback"
    }
}
