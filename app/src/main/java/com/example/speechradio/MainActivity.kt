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
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import java.util.UUID
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager

class MainActivity : AppCompatActivity() {
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var textViewResult: TextView
    private lateinit var buttonStartSpeech: Button
    private val PERMISSION_REQUEST_CODE = 1001


    object Constants
    {
        // Use a known UUID that both the phone and client agree upon.
        val MY_UUID: UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66")
    }


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textViewResult = findViewById(R.id.textViewResult)
        buttonStartSpeech = findViewById(R.id.buttonStartSpeech)

        //check for bluetooth
        val bluetoothManager = getSystemService(BluetoothManager::class.java)
        val bluetoothAdapter = bluetoothManager?.adapter

        if (bluetoothAdapter == null)
        {
            textViewResult.text = R.string.no_Blutooth.toString()
            return
        }

        if (!bluetoothAdapter.isEnabled)
        {
            textViewResult.text = R.string.Bluetooth_Off.toString()
            return
        }


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
                if (!results.isNullOrEmpty() )
                {
                    textViewResult.text = results[0] // Display recognized speech
                }
            }

            override fun onPartialResults(bundle: Bundle?) {}

            override fun onEvent(i: Int, bundle: Bundle?) {}
        })

        // Start speech recognition when button is clicked
        buttonStartSpeech.setOnClickListener { startSpeechRecognition() }
    }

    private fun checkAndRequestPermissions()
    {
        val requiredPermissions = mutableListOf<String>()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S)
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT)
                != PackageManager.PERMISSION_GRANTED)
            {
                requiredPermissions.add(Manifest.permission.BLUETOOTH_CONNECT)
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN)
                != PackageManager.PERMISSION_GRANTED)
            {
                requiredPermissions.add(Manifest.permission.BLUETOOTH_SCAN)
            }
        }
        else
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH)
                != PackageManager.PERMISSION_GRANTED)
            {
                requiredPermissions.add(Manifest.permission.BLUETOOTH)
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN)
                != PackageManager.PERMISSION_GRANTED)
            {
                requiredPermissions.add(Manifest.permission.BLUETOOTH_ADMIN)
            }
        }

        // Request location permission if needed
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED)
        {
            requiredPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        if (requiredPermissions.isNotEmpty())
        {
            ActivityCompat.requestPermissions(this, requiredPermissions.toTypedArray(), PERMISSION_REQUEST_CODE)
        }
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
