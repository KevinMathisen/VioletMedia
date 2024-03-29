package no.violetmedia

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import no.violetmedia.databinding.ActivityVideoPlayerBinding
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.SimpleExoPlayer

/**
 * Activity which plays a given video in a videoplayer
 */
class VideoPlayer : AppCompatActivity() {

    private lateinit var binding: ActivityVideoPlayerBinding
    private lateinit var player: ExoPlayer
    private var playbackPos: Long = 0
    private var currentWindow: Int = 0
    private var playWhenReady: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up binding
        binding = ActivityVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get source and subtitle
        val source = intent.getStringExtra("source") ?: ""
        val subtitle = intent.getStringExtra("subtitle")
        val subtitleType = intent.getStringExtra("subtitleType")

        // If the activity has a savedinstancestate, get the saved values
        if (savedInstanceState != null) {
            playbackPos = savedInstanceState.getLong("playbackPos")
            currentWindow = savedInstanceState.getInt("currentWindow")
            playWhenReady = savedInstanceState.getBoolean("playWhenReady")
        }

        // Set up and start playing the video from the source with subtitles if provided
        initializeExoPlayer(source, subtitle, subtitleType)

        // Hide systembars
        enterFullScreen()
    }

    /**
     * Initialize and configure exoplayer with the given url and subtitles
     *
     * @param url Url of the video. Can either be an online url or a local reference to a file
     * @param subtitle Url of an optional subtitle file
      */
    private fun initializeExoPlayer(url: String, subtitle: String?, subtitleType: String?) {

        // Build and link exoplayer to playerView
        player = ExoPlayer.Builder(this).build()
        val playerView = binding.playerView
        playerView.player = player

        // Parse url to an uri object
        val videoUri = Uri.parse(url)

        // Configure mediaitem from url or from url and subtitleUrl if given
        val mediaItem = if (subtitle == null) {
            MediaItem.fromUri(videoUri)
        } else {

            // Create subtitleConfig
            val subtitleUri = Uri.parse(subtitle)
            val subtitleConfig = MediaItem.SubtitleConfiguration.Builder(subtitleUri)
                .setMimeType(subtitleType)
                .setSelectionFlags(C.SELECTION_FLAG_DEFAULT)
                .build()
            // Create mediaitem
            MediaItem.Builder().setUri(videoUri).setSubtitleConfigurations(listOf(subtitleConfig)).build()
        }

        // Set up handling of playback error if video fails to load
        player.addListener(object: Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                // Delay the error message to ensure no overlap of toast message when launching the videoplayer
                Handler().postDelayed({
                    Toast.makeText(this@VideoPlayer, "Error loading video, the url provided does not work", Toast.LENGTH_LONG).show()
                }, 1000)
            }
        })

        // set mediaitem of exoplayer and other values based on saved state
        player.setMediaItem(mediaItem)
        player.prepare()
        player.playWhenReady = playWhenReady
        player.seekTo(currentWindow, playbackPos)


    }

    /**
     * Save playback values when onSaveInstanceState is called
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putLong("playbackPos", player.currentPosition)
        outState.putInt("currentWindow", player.currentMediaItemIndex)
        outState.putBoolean("playWhenReady", player.playWhenReady)
    }

    /**
     * Stop the player when the activity is stopped
     */
    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    /**
     * Exit fullscreen when the activity is destroyed
     */
    override fun onDestroy() {
        super.onDestroy()
        exitFullScreen()
    }

    /**
     * Releases the videoPlayer and saves its state
     */
    private fun releasePlayer() {
        playbackPos = player.currentPosition
        currentWindow = player.currentMediaItemIndex
        playWhenReady = player.playWhenReady
        player.release()
    }

    /**
     * Enters fullscreen by hiding status and navigation bars
     */
    private fun enterFullScreen() {
        // Hide the status bar and navigation bar
        WindowCompat.getInsetsController(window, window.decorView).hide(WindowInsetsCompat.Type.systemBars())
    }

    /**
     * Exits fullscreen by showing the status and navigation bars
     */
    private fun exitFullScreen() {
        // Show the status bar and navigation bar
        WindowCompat.getInsetsController(window, window.decorView).show(WindowInsetsCompat.Type.systemBars())
    }

}