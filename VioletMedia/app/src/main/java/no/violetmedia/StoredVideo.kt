package no.violetmedia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import no.violetmedia.databinding.ActivityStoredVideoBinding
class StoredVideo : AppCompatActivity() {
    private lateinit var binding: ActivityStoredVideoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoredVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

        var videos = mutableListOf(
            VideoList("Interstellar"),
            VideoList("Inception"),
            VideoList("Godfather"),
            VideoList("Up"),
            VideoList("Coco"),
            VideoList("Cars")
        )

        val adapter = VideoAdapter(videos)
        binding.rvVideos.adapter = adapter
        binding.rvVideos.layoutManager = LinearLayoutManager(this)

    }
}