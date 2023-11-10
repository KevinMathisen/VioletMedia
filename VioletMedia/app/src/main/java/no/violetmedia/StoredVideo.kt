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

/**
 * Activity for displaying videos stored, which launches activities for editing and playing videos on click
 */
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

        // Set up binding
        binding = ActivityStoredVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Initiate videoAdapter which will list all stored videos
        val videos = VideoDataManager.getVideos(applicationContext)
        val adapter = VideoAdapter(videos, this)
        binding.rvVideos.adapter = adapter
        binding.rvVideos.layoutManager = LinearLayoutManager(this)


        // On input in filter field, update videos displayed
        binding.etFilter.doOnTextChanged { _, _, _, _ ->
            val filter = binding.etFilter.text.toString()
            filterVideos(filter)
        }


        // Initiate receiver which will refresh videos displayed on update of list
        val receiver = object : BroadcastReceiver() {

            override fun onReceive(context: Context, intent: Intent) {
                // Update videos displayed
                val videosLocal = VideoDataManager.getVideos(applicationContext)
                val adapterLocal = VideoAdapter(videosLocal, binding.root.context)
                binding.rvVideos.adapter = adapterLocal
                // Reset filter
                binding.etFilter.setText("")
            }
        }


        val intentFilter = IntentFilter("RefreshStoredVideos")
        registerReceiver(receiver, intentFilter, RECEIVER_EXPORTED)

        /**
         * Initializes a TextToSpeech instance for speech synthesis.
         *
         * @param context application context.
         */
        textToSpeech = TextToSpeech(this) { status ->
            if (status != TextToSpeech.ERROR) {
                // Set the language
                textToSpeech.language = Locale.US
            }
        }

        // Request microphone permissions
        requestMicrophonePermission()

        // Call startspeechrecognition function on "Mic" button click
        binding.btnSpeech.setOnClickListener {
            startSpeechRecognition()
        }

        // Finish activity on "Back" button click
        binding.btnBack.setOnClickListener {
            finish()
        }


        /**
         * Sets UtteranceProgressListener for monitoring progress of speech synthesis
         *
         * @param object instance to monitor speech synthesis progress
         */
        textToSpeech.setOnUtteranceProgressListener(object: UtteranceProgressListener() {
            override fun onDone(utteranceId: String?){
                spokenText = utteranceId ?: ""
                filter = spokenText
                binding.etFilter.setText(filter)
                filterVideos(filter)
            }
            override fun onError(utteranceId: String?) {}
            override fun onStart(utteranceId: String?) {}
        })

    }

    /**
     * Requests microphone permission for speech recognition if not given
     */
    private fun requestMicrophonePermission() {
        val permission = Manifest.permission.RECORD_AUDIO
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), REQUEST_SPEECH_RECOGNITION)
        }
    }


    /**
     * initiates the speech recognition process
     */
    private fun startSpeechRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        startActivityForResult(intent, REQUEST_SPEECH_RECOGNITION)
    }

    /**
     * Handles the result of the speech recognition
     * Updates the list of videos based on speech output
     *
     * @param requestCode The request code to identify the operation
     * @param resultCode The result code indicating success or failure
     * @param data An Intent that may contain recognized speech results
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // If the users speech was successfully heard and processed
        if (requestCode == REQUEST_SPEECH_RECOGNITION && resultCode == RESULT_OK) {
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!results.isNullOrEmpty()) {

                // Get the input and update the filter with this
                spokenText = results[0]
                filter = spokenText
                binding.etFilter.setText(filter)
                filterVideos(filter)
            }
        }
    }

    /**
     * Filters vidoes displayed based on a given string
     *
     * @param query String to match video name and description with
     */
    private fun filterVideos(query : String?) {

        val videoList = VideoDataManager.getVideos(this)

        val filteredList = mutableListOf<VideoData>()

        // If user has entered a query
        if (query!=null) {

            // Go trough each video which exists and add them to the filtered list if they match the query
            videoList.forEach { item ->
                val name = item.name.lowercase()
                val description = item.description?.lowercase() ?: ""
                val keyword = query.lowercase()

                // If the video name or description contains the query, the video is added to the filtered list
                if (name.contains(keyword) || description.contains(keyword))  {
                    filteredList.add(item)
                }
            }

        }

        // Update the videos shown to the filtered list
        val adapter = VideoAdapter(filteredList, this)
        binding.rvVideos.adapter = adapter
        binding.rvVideos.layoutManager = LinearLayoutManager(this)
    }

    /**
     * Hides the keyboard if it is used
     */
    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (currentFocus != null) {
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    /**
     * Stops the text-to-speech when the activity is stopped
     */
    override fun onDestroy(){
        if (textToSpeech.isSpeaking){
        }
            textToSpeech.stop()
        textToSpeech.shutdown()

        super.onDestroy()
    }
}

