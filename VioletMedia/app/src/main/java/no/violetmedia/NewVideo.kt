package no.violetmedia

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

        // Initialize the shared preferences and editor
        sf = getSharedPreferences("my_sf", MODE_PRIVATE)
        editor = sf.edit()

        // Set an OnClickListener for the "Select Video" button (formerly btnSelectVideo)
        binding.btnSelectVideo.setOnClickListener {
            // Create an intent to open the file picker
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "video/*"
            startActivityForResult(intent, REQUEST_PICK_VIDEO)
        }

        // Set an OnClickListener for the "Back" button
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

    // onActivityResult method to handle the selected video
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_PICK_VIDEO && resultCode == Activity.RESULT_OK) {
            val selectedVideoUri = data?.data
            if (selectedVideoUri != null) {
                binding.etUrl.setText(selectedVideoUri.toString())

                Toast.makeText(this, "Video file found!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun newVideo() {
        val name: String = binding.etName.text.toString().trim()
        val url: String = binding.etUrl.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()

        if (name.isEmpty() || url.isEmpty()) {
            Toast.makeText(this, "Can't add video, have to specify name and URL", Toast.LENGTH_SHORT).show()
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

    companion object {
        private const val REQUEST_PICK_VIDEO = 1
    }
}