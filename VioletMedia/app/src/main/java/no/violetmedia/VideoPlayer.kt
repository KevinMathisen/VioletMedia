package no.violetmedia

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.media3.exoplayer.ExoPlayer
import no.violetmedia.databinding.ActivityVideoPlayerBinding
import androidx.media3.common.MediaItem

class VideoPlayer : AppCompatActivity() {

    private lateinit var binding: ActivityVideoPlayerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVideoPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val player = ExoPlayer.Builder(this).build()
        val playerView = binding.playerView

        playerView.player = player

        val uri = Uri.parse("https://storage.googleapis.com/wvmedia/clear/h264/tears/tears.mpd")
        val mediaItem = MediaItem.fromUri(uri)
        player.setMediaItem(mediaItem)

        player.prepare()
        player.play()
    }
}