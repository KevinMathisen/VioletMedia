package no.violetmedia

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
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

        if (savedInstanceState != null) {
            playbackPos = savedInstanceState.getLong("playbackPos")
            currentWindow = savedInstanceState.getInt("currentWindow")
            playWhenReady = savedInstanceState.getBoolean("playWhenReady")
        }

        initializeExoPlayer(source)
        //initializeExoPlayer("https://storage.googleapis.com/wvmedia/clear/h264/tears/tears.mpd")
    }

    private fun initializeExoPlayer(url: String) {
        player = ExoPlayer.Builder(this).build()
        val playerView = binding.playerView
        playerView.player = player

        val uri = Uri.parse(url)
        val mediaItem = MediaItem.fromUri(uri)
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

    private fun releasePlayer() {
        playbackPos = player.currentPosition
        currentWindow = player.currentMediaItemIndex
        playWhenReady = player.playWhenReady
        player.release()
    }
}