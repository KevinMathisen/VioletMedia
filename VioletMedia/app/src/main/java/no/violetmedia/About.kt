package no.violetmedia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import no.violetmedia.databinding.ActivityAboutBinding

/**
 * Activity which displays information about the application
 */
class About : AppCompatActivity() {
    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up binding
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set the names
        binding.tvAbout1.text = getString(R.string.names)

        // Set the instruction text
        binding.tvAbout2.text = getString(R.string.about)

        // Finish activity on "Back" button click
        binding.btnBack.setOnClickListener {
            finish()
        }

    }
}
