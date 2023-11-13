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

/**
 * Activity which creates a new video and adds it to persistent storage
 */
class NewVideo : AppCompatActivity() {

    private lateinit var binding: ActivityNewVideoBinding
    private lateinit var sf: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up binding
        binding = ActivityNewVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // run newVideo method on "Confirm" button click
        binding.btnConfirm.setOnClickListener {
            newVideo()
        }

        // Initialize shared preferences and editor
        sf = getSharedPreferences("my_sf", MODE_PRIVATE)
        editor = sf.edit()

        // Launch android get content on "Select Video (...)" button click
        binding.btnSelectVideo.setOnClickListener {

            // Create an intent to open the file picker
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "video/*"
            startActivityForResult(intent, REQUEST_PICK_VIDEO)
        }

        // Finish activity on back button click
        binding.btnBack.setOnClickListener {
            finish()
        }
    }
    /**
     * Saves the input entered in the input fields to shared preferences
     */
    override fun onPause() {
        super.onPause()

        val name = binding.etName.text.toString()
        val url = binding.etUrl.text.toString()
        val description = binding.etDescription.text.toString()
        val subtitleUrl = binding.etSubtitleUrl.text.toString()

        val editor = sf.edit()
        editor.putString("sf_name", name)
        editor.putString("sf_url", url)
        editor.putString("sf_description", description)
        editor.putString("sf_subtitle_url", subtitleUrl)

        editor.apply()
    }

    /**
     * @brief This method is called when the NewVideo activity is resumed.
     * The method retries the name from the shared preferences and sets it to the name EditText.
     */
    override fun onResume() {
        super.onResume()

        val name = sf.getString("sf_name", null)
        val url = sf.getString("sf_url", null)
        val description = sf.getString("sf_description", null)
        val subtitleUrl = sf.getString("sf_subtitle_url", null)

        binding.etName.setText(name)
        binding.etUrl.setText(url)
        binding.etDescription.setText(description)
        binding.etSubtitleUrl.setText(subtitleUrl)
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

                // Set the url field to be the source of the local file
                val url = selectedVideoUri.toString()
                val editor = sf.edit()
                editor.putString("sf_url", url)
                editor.apply()
                binding.etUrl.setText(url)


                Toast.makeText(this, "Video file found!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Copies the saved video to local storage, such that it is accessible after application restart
     *
     * @param uri Uri of video to add
     * @param name Name of video to add
     */
    private fun copyVideoToPrivateStorage(uri: Uri, name: String): String? {
        return try {
            // Create inputstream as input
            val inputStream = contentResolver.openInputStream(uri)
            // Create the output file
            val fileName = "$name.mp4"
            val outputFile = File(filesDir, fileName)

            // Copy the input to the outputfile
            FileOutputStream(outputFile).use { outputStream ->
                inputStream?.copyTo(outputStream)
            }

            // Returns the files path
            outputFile.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    /**
     * @brief Creates and saves a new video to persistent storage
     *
     * This method gets a videos details, validates the input, then creates a new video and saves it to persistent storage
     */
    private fun newVideo() {

        // Get user input
        val name: String = binding.etName.text.toString().trim()
        var url: String = binding.etUrl.text.toString().trim()
        val description = binding.etDescription.text.toString().trim()
        val subtitleUrl = binding.etSubtitleUrl.text.toString().trim()

        // Check if required fields are empty
        if (name.isEmpty() || url.isEmpty()) {
            Toast.makeText(this, "Can't add video, have to specify name and URL", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if the video already exists
        if (VideoDataManager.doesVideoExist(this, name)) {
            Toast.makeText(this, "Can't add video, video with name already exists", Toast.LENGTH_SHORT).show()
            return
        }

        // Create local video if local
        if (url.startsWith("content:")) {

            // Save video to local storage and get reference to it
            val uri = Uri.parse(url)
            val urlTemp = copyVideoToPrivateStorage(uri, name)

            // Use local reference, if not found inform user
            if (urlTemp == null) {
                Toast.makeText(this, "Can't add video, try to enter video source again", Toast.LENGTH_SHORT).show()
                return
            } else {
                url = urlTemp
            }
        }

        // Convert subtitle to null if empty
        val subtitle = if (subtitleUrl != "") subtitleUrl else null

        // Create new video instance and save it to storage
        val newVideo = VideoData(name, description, url, subtitle)
        val currentVideos = VideoDataManager.getVideos(this)
        currentVideos.add(newVideo)
        VideoDataManager.saveVideos(applicationContext, currentVideos)

        // Inform user of success
        Toast.makeText(this, "Video added successfully!", Toast.LENGTH_SHORT).show()

        // Clear all input field on success
        binding.etName.text.clear()
        binding.etUrl.text.clear()
        binding.etDescription.text.clear()
        binding.etSubtitleUrl.text.clear()
    }

    companion object {
        private const val REQUEST_PICK_VIDEO = 1
    }
}