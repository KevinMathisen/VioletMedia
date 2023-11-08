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

    // Initialize variables
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


        /**
         * @brief Initializes a TextToSpeech instance for speech synthesis.
         *
         * This method created a TextToSpeech instance, with a callback to handle the initialization status.
         * If the initialization is successful, it sets the language for the speech synthesis.
         *
         * @param context The application context.
         */
        textToSpeech = TextToSpeech(this) { status ->
            if (status != TextToSpeech.ERROR) {
                // Set the language, if needed
                textToSpeech.language = Locale.US // You can change this as needed
            }
        }

        // Calls to request microphone permissions
        requestMicrophonePermission()

        // Set an OnClickListener for the "Mic" button
        binding.btnSpeech.setOnClickListener {
            startSpeechRecognition()
        }

        // Set an OnClickListener for the "Back" button
        binding.btnBack.setOnClickListener {
            finish()
        }


        /**
         * @brief Sets UtteranceProgressListener for monitoring the progress of the speech synthesis.
         *
         * This method sets an UtteranceProgressListener to track the progress of the TextToSpeech engine.
         * When the synthesis is completed, it updates the 'spokenText' with the utterance ID or null.
         * It then applies the 'spokenText as a filter which is used in the filtering function.
         *
         * @param UtteranceProgressListener instance to monitor speech synthesis progress.
         */
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

        val videos = VideoDataManager.getVideos(applicationContext)
        val filter = IntentFilter("BroadcastReceiver")
        registerReceiver(receiver, filter, RECEIVER_EXPORTED)


        val adapter = VideoAdapter(videos, this)
        binding.rvVideos.adapter = adapter
        binding.rvVideos.layoutManager = LinearLayoutManager(this)

    }

    /**
     * @brief Requests microphone permission for speech recognition.
     *
     * This method checks if the app has the required microphone permissions to perform speech synthesis.
     * If it does not have permission already, it prompts the user of the application.
     */
    private fun requestMicrophonePermission() {
        val permission = Manifest.permission.RECORD_AUDIO
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), REQUEST_SPEECH_RECOGNITION)
        }
    }


    /**
     * @brief Initiates the speech recognition process.
     *
     * This method sets up and begins the speech recognition process.
     * This is done by launching an Intent to recognize speech input.
     */
    private fun startSpeechRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        startActivityForResult(intent, REQUEST_SPEECH_RECOGNITION)
    }

    /**
     * @brief Handles the result of the speech recognition.
     *
     * This method is called when the speech recognition Intent returns a result.
     * It also updates the list of videos, processes recognized speech input, and triggers filtering.
     *
     * @param requestCode The request code to identify the operation.
     * @param resultCode The result code indicating success or failure.
     * @param data An Intent that may contain recognized speech results.
     */
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

    /**
     * @brief Performs cleanup when the activity is destroyed.
     *
     * This method is called when the StoredActivity is being destroyed (shutdown or navigation).
     * It performs cleanup task such as stopping speech synthesis and the TextToSpeech engine.
     */
    override fun onDestroy(){
        if (textToSpeech.isSpeaking){
        }
            textToSpeech.stop()
        textToSpeech.shutdown()

        // Call the superclass's onDestroy method for additional cleanup.
        super.onDestroy()
    }
}

