package no.violetmedia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import no.violetmedia.databinding.ActivityEditVideoBinding


class EditVideo : AppCompatActivity() {
    private lateinit var binding: ActivityEditVideoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val orgName = intent.getStringExtra("source") ?: "N/A"

        var videos = VideoDataManager.getVideos(this)
        val video = videos.find { it.name == orgName }

        val source = video?.source ?: "N/A"
        val description = video?.description ?: "N/A"
        val subSource = video?.subtitle

        binding.tvEdit.text = "Edit ${orgName}"

        binding.etNameEdit.setText(orgName)
        binding.etDescriptionEdit.setText(description)
        binding.etUrlEdit.setText(source)

        if (subSource != null) {
            binding.etSubUrlEdit.setText(subSource)
        } else {
            binding.etSubUrlEdit.hint = "Subtitle Url not set"
        }

        binding.btnEditSave.setOnClickListener {
            val newName = binding.etNameEdit.text.toString()
            val newDesc = binding.etDescriptionEdit.text.toString()
            val newSource = binding.etUrlEdit.text.toString()
            val subtitleText = binding.etSubUrlEdit.text.toString()
            val subtitle = if (subtitleText != "") subtitleText else null

            val newVideo = VideoData(newName, newDesc, newSource, subtitle)
            videos = videos.map { if (it.name == orgName) newVideo else it }.toMutableList()

            VideoDataManager.saveVideos(applicationContext, videos)

            Toast.makeText(this, "Video saved!", Toast.LENGTH_SHORT).show()
        }

        binding.btnEditBack.setOnClickListener {
            finish()
        }

    }
}