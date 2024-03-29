package no.violetmedia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.media3.common.MimeTypes
import no.violetmedia.databinding.ActivityEditVideoBinding
import no.violetmedia.databinding.ConfirmDeleteDialogBinding
import java.io.File

/**
 * Activity class for editing a given video
  */
class EditVideo : AppCompatActivity() {
    private lateinit var binding: ActivityEditVideoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up binding
        binding = ActivityEditVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Get the name of the video we want to edit
        val orgName = intent.getStringExtra("source") ?: "N/A"

        // Get all videos stored, and retrive the video we want to edit
        var videos = VideoDataManager.getVideos(applicationContext)
        val video = videos.find { it.name == orgName }

        // Get the video details or default values
        val source = video?.source ?: "N/A"
        val description = video?.description ?: "N/A"
        val subSource = video?.subtitle


        // Set header text
        binding.tvEdit.text = "Edit ${orgName}"
        // Set text of name, description, and url fields
        binding.etNameEdit.setText(orgName)
        binding.etDescriptionEdit.setText(description)
        binding.etUrlEdit.setText(source)

        // Set suburl text or provide hint if the value was found
        if (subSource != null) {
            binding.etSubUrlEdit.setText(subSource)
        } else {
            binding.etSubUrlEdit.hint = "Subtitle Url not set"
        }


        // Save video with new details on button click
        binding.btnEditSave.setOnClickListener {
            editVideo(videos, orgName)
        }

        // Ask for confirmation of deletion on delete button click
        binding.btnEditDelete.setOnClickListener {
            askForDeleteConfirmation(orgName)
        }

        // Update videolist and finish activity on back button click
        binding.btnEditBack.setOnClickListener {
            // Create and send broadcast for refreshing videos displayed in the storedvideo activity
            val intent = Intent("RefreshStoredVideos")
            sendBroadcast(intent)

            finish()
        }
    }


    /**
     * Function that validates the changes in edit video, and if acceptable edits the video information
     * This information is then saved in persistent storage
     *
     * @param vidoes The specific video we want to apply changes to
     * @param orgName The original name of the specific video
     */
    private fun editVideo(videos: MutableList<VideoData>, orgName: String) {
        // Get all values entered by the user
        val newName = binding.etNameEdit.text.toString()
        val newDesc = binding.etDescriptionEdit.text.toString()
        val newSource = binding.etUrlEdit.text.toString()
        val subtitleText = binding.etSubUrlEdit.text.toString()

        // Check if the name and source are not empty
        if (newName.isEmpty() || newSource.isEmpty()) {
            Toast.makeText(this, "Can't save, video has to have name and URL", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if the newname is already used by another video
        if (newName != orgName && VideoDataManager.doesVideoExist(this, newName)) {
            Toast.makeText(this, "Can't save video, video with name already exists", Toast.LENGTH_SHORT).show()
            return
        }

        var subtitle: String? = null
        var subtitleType: String? = null
        if (subtitleText != "") {
            subtitle = subtitleText

            // Set subtitle type
            subtitleType = when {
                subtitleText.endsWith(".vtt") -> MimeTypes.TEXT_VTT
                subtitleText.endsWith(".xml") -> MimeTypes.APPLICATION_TTML
                subtitleText.endsWith(".ttml") -> MimeTypes.APPLICATION_TTML
                subtitleText.endsWith(".ass") -> MimeTypes.TEXT_SSA
                subtitleText.endsWith(".srt") -> MimeTypes.APPLICATION_SUBRIP
                else -> "unknown"
            }

            // Cancel edit if format not known
            if (subtitleType == "unknown") {
                Toast.makeText(this, "Can't save video with selected subtitle, invalid subtitle format", Toast.LENGTH_SHORT).show()
                return
            }
        }


        // Create a new video with the given details
        val newVideo = VideoData(newName, newDesc, newSource, subtitle, subtitleType)
        // Replace the video edited in the video list
        val videosFiltered = videos.map { if (it.name == orgName) newVideo else it }.toMutableList()

        // Save the updated videolist with the edited video
        VideoDataManager.saveVideos(applicationContext, videosFiltered)

        // Inform user of success
        Toast.makeText(this, "Video saved!", Toast.LENGTH_SHORT).show()
    }

    /**
     * Method which asks the user for confirmation when trying to delete a video.
     * If they click yes, delete the video.
     *
     * @param videoName Name of video to delete
     */
    private fun askForDeleteConfirmation(videoName: String) {

        // Create and get binding reference to a conformation dialog
        val binding = ConfirmDeleteDialogBinding.inflate(layoutInflater)
        val confirmView = AlertDialog.Builder(this).setView(binding.root).create()

        // Dismiss dialog on cancel button click
        binding.cancelButton.setOnClickListener {
            confirmView.dismiss()
        }

        // Delete video given on confirm button click
        binding.confirmButton.setOnClickListener {

            // Tries to delete the video from persistent storage if it is local, if not nothing happens
            deleteLocalFile(videoName)

            // Get videos, remove selected video, and save the new list
            var videos = VideoDataManager.getVideos(applicationContext)
            videos = videos.filterNot { it.name == videoName }.toMutableList()
            VideoDataManager.saveVideos(applicationContext, videos)

            // Dismiss the dialog
            confirmView.dismiss()

            // Create and send broadcast for refreshing videos displayed in the storedvideo activity
            val intent = Intent("RefreshStoredVideos")
            sendBroadcast(intent)

            finish()
        }

        // Show the confirmDialog
        confirmView.show()
    }

    /**
     * Deletes a local file if it exists
     *
     * @param name Name of file to be deleted
     */
    private fun deleteLocalFile(name: String) {
        // Create file object to delete
        val fileName = "$name.mp4"
        val fileToDelete = File(filesDir, fileName)

        // If file exists, delete it
        if (fileToDelete.exists()) {
            fileToDelete.delete()
        }
    }
}