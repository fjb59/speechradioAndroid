package com.example.speechradio


import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.speech.SpeechRecognizer.createSpeechRecognizer
import android.speech.RecognizerIntent
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity() {
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var textViewResult: TextView
    private lateinit var buttonStartSpeech: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textViewResult = findViewById(R.id.textViewResult)
        buttonStartSpeech = findViewById(R.id.buttonStartSpeech)

        // Check for permissions
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), 1)

        // Initialize SpeechRecognizer
        speechRecognizer = createSpeechRecognizer(this)
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(bundle: Bundle?) {
                Toast.makeText(applicationContext, "Listening...", Toast.LENGTH_SHORT).show()
            }

            override fun onBeginningOfSpeech() {}

            override fun onRmsChanged(v: Float) {}

            override fun onBufferReceived(bytes: ByteArray?) {}

            override fun onEndOfSpeech() {}

            override fun onError(i: Int) {
                Toast.makeText(applicationContext, "Error recognizing speech", Toast.LENGTH_SHORT).show()
            }

            override fun onResults(bundle: Bundle?) {
                val results = bundle?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (results != null && results.isNotEmpty()) {
                    textViewResult.text = results[0] // Display recognized speech
                }
            }

            override fun onPartialResults(bundle: Bundle?) {}

            override fun onEvent(i: Int, bundle: Bundle?) {}
        })

        // Start speech recognition when button is clicked
        buttonStartSpeech.setOnClickListener { startSpeechRecognition() }
    }

    private fun startSpeechRecognition() {
        val recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")
        }
        try {
            speechRecognizer.startListening(recognizerIntent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(applicationContext, "Speech recognition not supported", Toast.LENGTH_SHORT).show()
        }
    }
}
