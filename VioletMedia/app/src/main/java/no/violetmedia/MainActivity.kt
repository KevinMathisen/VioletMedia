package no.violetmedia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import no.violetmedia.databinding.ActivityMainBinding
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set an OnClickListener for the "About" button
        binding.btnAbout.setOnClickListener {
            val intent = Intent(this, About::class.java)
            startActivity(intent)
        }

        // Set an OnClickListener for the "New Video" button
        binding.btnNewVideo.setOnClickListener {
            val intent = Intent(this, NewVideo::class.java)
            startActivity(intent)
        }

        // Set an OnClickListener for the "Your Video" button
        binding.btnStoredVideo.setOnClickListener {
            val intent = Intent(this, StoredVideo::class.java)
            startActivity(intent)
        }
    }

}