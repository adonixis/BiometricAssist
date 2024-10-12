package ru.adonixis.biometricassist.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import java.util.Locale

class VoiceAnalyzer(
    context: Context,
    private val onVoiceRecognized: (VoiceQuality) -> Unit,
) {
    private var text: String = ""
    private var rms: Float? = null
    private var isOnlyNumbers: Boolean? = null
    private var isOnlyRussianLanguage: Boolean? = null

    private val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
        setRecognitionListener(object : RecognitionListener {

            override fun onReadyForSpeech(params: Bundle?) {
                Log.d("Speech Recognizer", "onReadyForSpeech: $params")
            }

            override fun onBeginningOfSpeech() {
                Log.d("Speech Recognizer", "onBeginningOfSpeech")
            }

            override fun onRmsChanged(rmsdB: Float) {
                Log.d("Speech Recognizer", "onRmsChanged: $rmsdB")
                rms = rmsdB

                onVoiceRecognized(
                    VoiceQuality(
                        text = text,
                        signalNoise = rmsdB,
                        russianLanguage = isOnlyRussianLanguage,
                        onlyNumbers = isOnlyNumbers
                    )
                )
            }

            override fun onBufferReceived(buffer: ByteArray?) {
                Log.d("Speech Recognizer", "onBufferReceived: $buffer")
            }

            override fun onEndOfSpeech() {
                Log.d("Speech Recognizer", "onEndOfSpeech")
            }

            override fun onError(error: Int) {
                val message = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Audio"
                    SpeechRecognizer.ERROR_CANNOT_CHECK_SUPPORT -> "Cannot Check Support"
                    SpeechRecognizer.ERROR_CLIENT -> "Client"
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient Permissions"
                    SpeechRecognizer.ERROR_LANGUAGE_NOT_SUPPORTED -> "Language Not Supported"
                    SpeechRecognizer.ERROR_LANGUAGE_UNAVAILABLE -> "Language Unavailable"
                    SpeechRecognizer.ERROR_NETWORK -> "Network"
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Network Timeout"
                    SpeechRecognizer.ERROR_NO_MATCH -> "No Match"
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Busy"
                    SpeechRecognizer.ERROR_SERVER -> "Server Error"
                    SpeechRecognizer.ERROR_SERVER_DISCONNECTED -> "Server Disconnected"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Speech Timeout"
                    SpeechRecognizer.ERROR_TOO_MANY_REQUESTS -> "Too Many Requests"
                    else -> "Unknown"
                }
                Log.e("Speech Recognizer", "STT Error: $message")
            }

            override fun onResults(results: Bundle?) {
                val result = results
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.getOrNull(0) ?: ""

                Log.d("Speech Recognizer", "onResults: $result")
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val partial = partialResults
                    ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                    ?.getOrNull(0) ?: ""

                Log.d("Speech Recognizer", "onPartialResult: $partial")

                text += partial
                isOnlyNumbers = isOnlyRightNumbers(text)
                isOnlyRussianLanguage = isOnlyRussianLanguage(text)
                onVoiceRecognized(
                    VoiceQuality(
                        text = partial,
                        signalNoise = rms,
                        russianLanguage = isOnlyRussianLanguage,
                        onlyNumbers = isOnlyNumbers
                    )
                )
            }

            override fun onEvent(eventType: Int, params: Bundle?) {
                Log.d("Speech Recognizer", "onEvent: $eventType, $params")
            }
        })
    }

    private val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            Locale.getDefault()
        )
        putExtra(
            RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,
            10000
        )
    }

    private fun isOnlyRightNumbers(text: String?): Boolean? {
        if (text.isNullOrEmpty()) return null
        var textWithOnlyWords: String = text
        DIGITS.forEachIndexed { index, digit ->
            textWithOnlyWords = textWithOnlyWords.replace(digit.toString(), " " + RUSSIAN_DIGITS[index] + " ")
        }
        val chunks = textWithOnlyWords.trim().split("[\\s-—]+".toRegex())
        if (chunks.size > DIGITS.size) return false
        chunks.forEachIndexed { index, chunk ->
            val trimmedChunk = chunk.trim()
            if (trimmedChunk != DIGITS[index].toString() && trimmedChunk != RUSSIAN_DIGITS[index]) {
                return false
            }
        }
        return true
    }

    private fun isOnlyRussianLanguage(text: String?): Boolean? {
        if (text.isNullOrEmpty()) return null
        text.forEach { char ->
            if (char.isLetter() && !RUSSIAN_ALPHABET.contains(char.lowercaseChar()))
                return false
        }
        return true
    }

    fun startListening() {
        speechRecognizer.startListening(intent)
    }

    fun stopListening() {
        text = ""
        rms = null
        isOnlyNumbers = null
        speechRecognizer.stopListening()
    }

    companion object {
        private const val RUSSIAN_ALPHABET = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя"
        private val RUSSIAN_DIGITS = arrayOf("ноль", "один", "два", "три", "четыре", "пять", "шесть", "семь", "восемь", "девять")
        private val DIGITS = arrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
    }
}
