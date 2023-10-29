package no.violetmedia

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import no.violetmedia.databinding.ActivityNewVideoBinding

class NewVideo : AppCompatActivity() {
    private lateinit var binding: ActivityNewVideoBinding
    private lateinit var sf: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnConfirm.setOnClickListener {
            newVideo()
        }

        sf = getSharedPreferences("my_sf", MODE_PRIVATE)
        editor = sf.edit()

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onPause() {
        super.onPause()
        val name = binding.etName.text.toString()
        editor.putString("sf_name", name).apply()
    }

    override fun onResume() {
        super.onResume()
        val name = sf.getString("sf_name", null)
        binding.etName.setText(name)
    }


    private fun newVideo() {
        val name: String = binding.etName.text.toString().trim()
        val url: String = binding.etUrl.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()

        if (name.isEmpty() || url.isEmpty()) {
            Toast.makeText(this, "Can't add video, have to specify name and url", Toast.LENGTH_SHORT).show()
            return
        }

        if (VideoDataManager.doesVideoExist(this, name)) {
            Toast.makeText(this, "Can't add video, video with name already exists", Toast.LENGTH_SHORT).show()
            return
        }

        val newVideo = VideoData(name, description, url, false)
        val currentVideos = VideoDataManager.getVideos(this).toMutableList()
        currentVideos.add(newVideo)
        VideoDataManager.saveVideos(this, currentVideos)

        Toast.makeText(this, "Video added successfully!", Toast.LENGTH_SHORT).show()

        binding.etName.text.clear()
        binding.etUrl.text.clear()
        binding.etDescription.text.clear()
    }
}