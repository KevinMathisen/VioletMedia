package no.violetmedia

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import no.violetmedia.databinding.ActivityVideoPlayerBinding
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.SimpleExoPlayer

class VideoPlayer : AppCompatActivity() {

    private lateinit var binding: ActivityVideoPlayerBinding
    private lateinit var player: ExoPlayer
    private var playbackPos: Long = 0
    private var currentWindow: Int = 0
    private var playWhenReady: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val source = intent.getStringExtra("source") ?: ""
        val subtitle = intent.getStringExtra("subtitle")

        if (savedInstanceState != null) {
            playbackPos = savedInstanceState.getLong("playbackPos")
            currentWindow = savedInstanceState.getInt("currentWindow")
            playWhenReady = savedInstanceState.getBoolean("playWhenReady")
        }

        initializeExoPlayer(source, subtitle)
        //initializeExoPlayer("https://storage.googleapis.com/wvmedia/clear/h264/tears/tears.mpd")

        enterFullScreen()
    }

    private fun initializeExoPlayer(url: String, subtitle: String?) {
        player = ExoPlayer.Builder(this).build()
        val playerView = binding.playerView
        playerView.player = player

        val videoUri = Uri.parse(url)

        val mediaItem = if (subtitle == null) {
            MediaItem.fromUri(videoUri)
        } else {
            val subtitleUri = Uri.parse(subtitle)
            val subtitle = MediaItem.SubtitleConfiguration.Builder(subtitleUri)
                .setMimeType("text/vtt")
                .setSelectionFlags(C.SELECTION_FLAG_DEFAULT)
                .build()
            MediaItem.Builder().setUri(videoUri).setSubtitleConfigurations(listOf(subtitle)).build()
        }

        player.setMediaItem(mediaItem)

        player.prepare()
        player.playWhenReady = playWhenReady
        player.seekTo(currentWindow, playbackPos)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putLong("playbackPos", player.currentPosition)
        outState.putInt("currentWindow", player.currentMediaItemIndex)
        outState.putBoolean("playWhenReady", player.playWhenReady)
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        exitFullScreen()
    }

    private fun releasePlayer() {
        playbackPos = player.currentPosition
        currentWindow = player.currentMediaItemIndex
        playWhenReady = player.playWhenReady
        player.release()
    }

    private fun enterFullScreen() {
        // Hide the status bar and navigation bar
        WindowCompat.getInsetsController(window, window.decorView).hide(WindowInsetsCompat.Type.systemBars())
    }

    private fun exitFullScreen() {
        // Show the status bar and navigation bar
        WindowCompat.getInsetsController(window, window.decorView).show(WindowInsetsCompat.Type.systemBars())
    }

}