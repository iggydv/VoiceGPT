package com.me.voicegpt

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import javax.sound.sampled.AudioFileFormat

@RestController
class RecordingApi @Autowired constructor(
    private val soundRecorder: SoundRecorder,
    private val audioFormatConfig: AudioFormatConfig,
    private val speechToText: SpeechToText,
    private val chatGPT: ChatGPT
) {
    @GetMapping("/ask/start")
    fun startRecording() {
        println("Start recording ....")
        val format = audioFormatConfig.buildAudioFormatInstance()
        soundRecorder.build(format)
        soundRecorder.start()
    }

    @GetMapping("/ask/stop")
    fun stopRecording() {
        println("Stop recording ....")
        soundRecorder.stop()
        val wd = WaveDataUtil()
        Thread.sleep(1000)
        val fileName = wd.saveToFile("recordings/SoundClip", AudioFileFormat.Type.WAVE, soundRecorder.audioInputStream)
        if (fileName.isNotEmpty()) {
            val text = speechToText.transcribe(fileName)
            chatGPT.askChatGPT(text)
        }
    }
}