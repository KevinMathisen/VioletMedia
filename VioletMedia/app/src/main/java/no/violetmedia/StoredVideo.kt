package no.violetmedia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Adapter
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import no.violetmedia.databinding.ActivityStoredVideoBinding
import android.content.Context
import android.view.inputmethod.InputMethodManager
class StoredVideo : AppCompatActivity() {

    private lateinit var binding: ActivityStoredVideoBinding
    private lateinit var adapter: Adapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoredVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val videos = VideoDataManager.getVideos(this)

        binding.btnBack.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }

        binding.btnFilter.setOnClickListener {
            hideKeyboard()
            var filter = binding.etFilter.text.toString()
            filterStuff(filter)
        }

        binding.btnClear.setOnClickListener {
            videos.clear()
            VideoDataManager.saveVideos(this, videos)
            val adapter = VideoAdapter(videos)
            binding.rvVideos.adapter = adapter
            binding.rvVideos.layoutManager = LinearLayoutManager(this)
        }

        val adapter = VideoAdapter(videos)
        binding.rvVideos.adapter = adapter
        binding.rvVideos.layoutManager = LinearLayoutManager(this)

    }
    private fun filterStuff(query : String?) {

        val videoList = VideoDataManager.getVideos(this)

        val filteredList = mutableListOf<VideoData>()
        if (query!=null) {

            videoList.forEach { element ->
                val a = element.name.toLowerCase()
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

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (currentFocus != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }
}