package no.violetmedia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import no.violetmedia.databinding.ActivitySettingsBinding

class Settings : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvAbout1.text = getString(R.string.names)

        binding.tvAbout2.text = getString(R.string.about)

        binding.btnBack.setOnClickListener {
            val videos = VideoDataManager.getVideos(this)
            videos.clear()
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }



    }
}
