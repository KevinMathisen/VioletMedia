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


        binding.btnSettings.setOnClickListener {
            val intent = Intent(this,Settings::class.java)
            startActivity(intent)
        }

        binding.btnNewVideo.setOnClickListener {
            val intent = Intent(this,NewVideo::class.java)
            startActivity(intent)
        }

        binding.btnStoredVideo.setOnClickListener {
            val intent = Intent(this,StoredVideos::class.java)
            startActivity(intent)
        }

    }
}