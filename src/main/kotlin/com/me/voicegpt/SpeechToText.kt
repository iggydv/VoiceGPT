package com.me.voicegpt

import com.google.cloud.speech.v1.*
import com.google.protobuf.ByteString
import org.springframework.stereotype.Component
import java.io.File
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem

@Component
class SpeechToText {

    fun convertSoundToBytes(filename: String): ByteString {
        val input = AudioSystem.getAudioInputStream(File(filename))
        return audioInputStreamToByteString(input)
    }

    fun audioInputStreamToByteString(audioInputStream: AudioInputStream): ByteString {
        return ByteString.readFrom(audioInputStream)
    }

    fun transcribe(file: String): String {
        return transcribe(convertSoundToBytes(file))
    }

    fun transcribe(audioInputStream: AudioInputStream): String {
        return transcribe(audioInputStreamToByteString(audioInputStream))
    }

    fun transcribe(byteString: ByteString): String {
        val kv: HashMap<Float, String> = HashMap()
        SpeechClient.create().use { speechClient ->
            // Builds the sync recognize request
            val config = RecognitionConfig.newBuilder()
                .setEncoding(RecognitionConfig.AudioEncoding.LINEAR16)
                .setSampleRateHertz(44100)
                .setLanguageCode("en-GB")
                .build()
            val audio = RecognitionAudio.newBuilder().setContent(byteString).build()
            val response: RecognizeResponse = speechClient.recognize(config, audio)
            val results: List<SpeechRecognitionResult> = response.getResultsList()

            for (result in results) {
                for (alt in result.alternativesList) {
                    // There can be several alternative transcripts for a given chunk of speech. Just use the
                    // first (most likely) one here.
                    val transcript = alt.transcript
                    val confidence = alt.confidence
                    kv[confidence] = transcript
                    println("Transcription: $transcript [confidence: $confidence]")
                    return transcript
                }
            }
        }
        return ""
    }
}