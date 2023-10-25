package no.violetmedia

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import no.violetmedia.databinding.ActivityNewVideoBinding

class NewVideo : AppCompatActivity() {
    private lateinit var binding: ActivityNewVideoBinding
    private lateinit var sf: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var name2: String = binding.etName.toString().trim()
        var url2: String = binding.etUrl.toString().trim()
        //Legg til description

        binding.btnConfirm.setOnClickListener {
            if (name2.isNotEmpty() && url2.isNotEmpty()){
                val newVideo = VideoData(name2, null, url2, false)
                val currentVideos = VideoDataManager.getVideos(this).toMutableList()
                currentVideos.add(newVideo)
                VideoDataManager.saveVideos(this, currentVideos)

                Toast.makeText(this, "Video added sucessfully!", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "Can't add video", Toast.LENGTH_SHORT).show()
            }
        }

        sf = getSharedPreferences("my_sf", MODE_PRIVATE)
        editor = sf.edit()

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
    override fun onPause() {
        super.onPause()
        val name = binding.etName.text.toString()
        editor.putString("sf_name", name).apply()
    }

    override fun onResume() {
        super.onResume()
        val name = sf.getString("sf_name", null)
        binding.etName.setText(name)
    }
}