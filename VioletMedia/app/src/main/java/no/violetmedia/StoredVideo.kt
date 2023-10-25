package no.violetmedia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Adapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import no.violetmedia.databinding.ActivityStoredVideoBinding
class StoredVideo : AppCompatActivity() {

    private lateinit var binding: ActivityStoredVideoBinding
    private lateinit var adapter: Adapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoredVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

        binding.btnFilter.setOnClickListener {
            var filter = binding.etFilter.text.toString()
            filterStuff(filter)
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
    private fun filterStuff(query : String?) {
        val videoList = mutableListOf(
            VideoList("Interstellar"),
            VideoList("Inception"),
            VideoList("Godfather"),
            VideoList("Up"),
            VideoList("Coco"),
            VideoList("Cars")
        )

        val filteredList = mutableListOf<VideoList>()
        if (query!=null) {

            videoList.forEach { element ->
                val a = element.title.toLowerCase()
                val b = query.toLowerCase()
                if (a.contains(b)) {
                    filteredList.add(element)
                }
            }

        }

        if(filteredList.isEmpty()) {
            Toast.makeText(this, "No videos found matching " +
                    "this filter", Toast.LENGTH_SHORT).show()
            return
        }
        else{
            val adapter = VideoAdapter(filteredList)
            binding.rvVideos.adapter = adapter
            binding.rvVideos.layoutManager = LinearLayoutManager(this)
        }
    }
}