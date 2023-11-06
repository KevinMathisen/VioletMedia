package no.violetmedia

import android.Manifest
import android.content.BroadcastReceiver
import android.speech.RecognizerIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.view.inputmethod.InputMethodManager
import android.widget.Adapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import no.violetmedia.databinding.ActivityStoredVideoBinding
import java.util.Locale

class StoredVideo : AppCompatActivity() {

    private lateinit var textToSpeech: TextToSpeech
    private lateinit var spokenText: String
    private lateinit var binding: ActivityStoredVideoBinding
    private lateinit var adapter: Adapter

    companion object {
        private const val REQUEST_SPEECH_RECOGNITION = 1
    }
    private var filter: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoredVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        textToSpeech = TextToSpeech(this) { status ->
            if (status != TextToSpeech.ERROR) {
                // Set the language, if needed
                textToSpeech.language = Locale.US // You can change this as needed
            }
        }
        val videos = VideoDataManager.getVideos(applicationContext)


        requestMicrophonePermission()

        binding.btnSpeech.setOnClickListener {
            startSpeechRecognition()
        }

        textToSpeech.setOnUtteranceProgressListener(object: UtteranceProgressListener() {
            override fun onDone(utteranceId: String?){
                spokenText = utteranceId ?: ""
                filter = spokenText
                filterVideos(filter)

            }
            override fun onError(utteranceId: String?) {
                // Handle any errors if needed
            }

            override fun onStart(utteranceId: String?) {
                // Handle the start of speech if needed
            }
        })

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.etFilter.doOnTextChanged { _, _, _, _ ->
            val filter = binding.etFilter.text.toString()
            filterVideos(filter)
        }

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val videos = VideoDataManager.getVideos(applicationContext)
                val adapter = VideoAdapter(videos, binding.root.context)
                binding.rvVideos.adapter = adapter
                binding.etFilter.setText("")
            }
        }
        val filter = IntentFilter("BroadcastReceiver")
        registerReceiver(receiver, filter, RECEIVER_EXPORTED)


        val adapter = VideoAdapter(videos, this)
        binding.rvVideos.adapter = adapter
        binding.rvVideos.layoutManager = LinearLayoutManager(this)

    }

    private fun requestMicrophonePermission() {
        val permission = Manifest.permission.RECORD_AUDIO
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), REQUEST_SPEECH_RECOGNITION)
        }
    }

    private fun startSpeechRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        startActivityForResult(intent, REQUEST_SPEECH_RECOGNITION)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val videos = VideoDataManager.getVideos(applicationContext)
        binding.rvVideos.adapter = VideoAdapter(videos, this)

        if (requestCode == REQUEST_SPEECH_RECOGNITION && resultCode == RESULT_OK) {
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!results.isNullOrEmpty()) {
                spokenText = results[0]
                filter = spokenText
                binding.etFilter.setText(filter)
                filterVideos(filter)
            }
        }
    }
    private fun filterVideos(query : String?) {

        val videoList = VideoDataManager.getVideos(this)

        val filteredList = mutableListOf<VideoData>()
        if (query!=null) {

            videoList.forEach { item ->
                val name = item.name.lowercase()
                val description = item.description?.lowercase() ?: ""
                val keyword = query.lowercase()
                if (name.contains(keyword) || description.contains(keyword))  {
                    filteredList.add(item)
                }
            }

        }

        val adapter = VideoAdapter(filteredList, this)
        binding.rvVideos.adapter = adapter
        binding.rvVideos.layoutManager = LinearLayoutManager(this)
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (currentFocus != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    override fun onDestroy(){
        if (textToSpeech.isSpeaking){
            textToSpeech.stop()
        }
        textToSpeech.shutdown()
        super.onDestroy()
    }
}

