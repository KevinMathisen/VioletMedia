package no.violetmedia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import no.violetmedia.databinding.ActivityEditVideoBinding


class EditVideo : AppCompatActivity() {
    private lateinit var binding: ActivityEditVideoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val item = intent.getSerializableExtra("ITEM") as? VideoData

        binding.tvEdit.text = "Edit ${item?.name}"

        binding.etNameEdit.setText(item?.name)
        binding.etDescriptionEdit.setText(item?.description)
        binding.etUrlEdit.setText(item?.source)

        binding.btnEditSave.setOnClickListener {
            item?.let { // Ensure item is not null
                it.name = binding.etNameEdit.text.toString()
                it.description = binding.etDescriptionEdit.text.toString()
                it.source = binding.etUrlEdit.text.toString()

                val intent = Intent(this, StoredVideo::class.java)
                startActivity(intent)
            }
        }


        binding.btnEditSave.setOnClickListener {
            val intent = Intent(this,StoredVideo::class.java)
            startActivity(intent)
        }

    }
}