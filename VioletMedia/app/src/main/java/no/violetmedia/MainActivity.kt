package no.violetmedia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import no.violetmedia.databinding.ActivityMainBinding

/**
 * Activity class for the front page
 */
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Launch about activity on "About" button click
        binding.btnAbout.setOnClickListener {
            val intent = Intent(this, About::class.java)
            startActivity(intent)
        }

        //  Launch new video activity on "new video" button click
        binding.btnNewVideo.setOnClickListener {
            val intent = Intent(this, NewVideo::class.java)
            startActivity(intent)
        }

        // Launch Stored video activity on "Stored Videos" button click
        binding.btnStoredVideo.setOnClickListener {
            val intent = Intent(this, StoredVideo::class.java)
            startActivity(intent)
        }
    }

}