package no.violetmedia

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import no.violetmedia.databinding.ActivityNewVideoBinding
import java.io.File
import java.io.FileOutputStream

class NewVideo : AppCompatActivity() {

    // Initialize variables
    private lateinit var binding: ActivityNewVideoBinding
    private lateinit var sf: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set an OnClickListener for the "Confirm" button
        binding.btnConfirm.setOnClickListener {
            newVideo()
        }

        // Initialize the shared preferences and editor
        sf = getSharedPreferences("my_sf", MODE_PRIVATE)
        editor = sf.edit()

        // Set an OnClickListener for the "Select Video (...)" button
        binding.btnSelectVideo.setOnClickListener {

            // Create an intent to open the file picker
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "video/*"
            startActivityForResult(intent, REQUEST_PICK_VIDEO)
        }

        // Set an OnClickListener for the "Back" button
        binding.btnBack.setOnClickListener {
            finish()
        }
    }
    /**
    @brief This method is called when the NewVideo activity is paused.
     The method saves the name entered in the name EditText field to shared preferences.
     */
    override fun onPause() {
        super.onPause()
        val name = binding.etName.text.toString()
        editor.putString("sf_name", name).apply()
    }

    /**
     * @brief This method is called when the NewVideo activity is resumed.
     * The method retries the name from the shared preferences and sets it to the name EditText.
     */
    override fun onResume() {
        super.onResume()
        val name = sf.getString("sf_name", null)
        binding.etName.setText(name)
    }

    /**
     * @brief Overrides the 'onActivityResult' method to handle the result of picking a video.
     * The method is called when an activity started with 'startActivityForResult' returns a result.
     *
     * @param requestCode The code that originally supplied 'startActivityForResult'.
     * @param resultCode The result code returned from the child activity.
     * @param data An Intent that carries the resulting data (URI).
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_PICK_VIDEO && resultCode == Activity.RESULT_OK) {
            val selectedVideoUri = data?.data
            if (selectedVideoUri != null) {
                val videoPath = copyVideoToPrivateStorage(selectedVideoUri)
                binding.etUrl.setText(videoPath)

                Toast.makeText(this, "Video file found!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * @brief
     * Text---
     *
     * @param uri
     */
    private fun copyVideoToPrivateStorage(uri: Uri): String? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val outputFile = File(filesDir, "video.mp4")

            FileOutputStream(outputFile).use { outputStream ->
                inputStream?.copyTo(outputStream)
            }

            outputFile.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    /**
     * @brief Adds a new video to the library: Your Videos
     *
     * This method collects data about a new video, name, description, url, subtitle
     * It also validates the user inputs to ensure that the url is valid and the name is unique
     * If these checks pass, the video information is stored, and we get a success message
     */
    private fun newVideo() {
        val name: String = binding.etName.text.toString().trim()
        val url: String = binding.etUrl.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val subtitleUrl = binding.etSubtitleUrl.text.toString().trim()

        if (name.isEmpty() || url.isEmpty()) {
            Toast.makeText(this, "Can't add video, have to specify name and URL", Toast.LENGTH_SHORT).show()
            return
        }

        if (VideoDataManager.doesVideoExist(this, name)) {
            Toast.makeText(this, "Can't add video, video with name already exists", Toast.LENGTH_SHORT).show()
            return
        }

       val subtitle = if (subtitleUrl != "") subtitleUrl else null

        val newVideo = VideoData(name, description, url, subtitle)
        val currentVideos = VideoDataManager.getVideos(this).toMutableList()
        currentVideos.add(newVideo)
        VideoDataManager.saveVideos(applicationContext, currentVideos)

        Toast.makeText(this, "Video added successfully!", Toast.LENGTH_SHORT).show()

        binding.etName.text.clear()
        binding.etUrl.text.clear()
        binding.etDescription.text.clear()
    }

    companion object {
        private const val REQUEST_PICK_VIDEO = 1
    }
}