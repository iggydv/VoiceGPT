package com.me.voicegpt

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import javax.sound.sampled.*

@Component
class SoundRecorder @Autowired constructor(private val speechToText: SpeechToText) : Runnable {
    lateinit var format: AudioFormat

    fun build(format: AudioFormat): SoundRecorder {
        this.format = format
        return this
    }

    private lateinit var thread: Thread
    private var duration = 0.0
    lateinit var audioInputStream: AudioInputStream
    lateinit var transcribedMessage: String
    var record = false

    fun start() {
        thread = Thread(this)
        record = true
        thread.name = "Capture Microphone"
        thread.start()
    }

    fun stop() {
        thread.interrupt()
        record = false
    }

    override fun run() {
        duration = 0.0
        val line = getTargetDataLineForRecord()
        val out = ByteArrayOutputStream()
        try {
            val frameSizeInBytes = format.frameSize
            val bufferLengthInFrames = line.bufferSize / 8
            val bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes
            buildByteOutputStream(out, line, frameSizeInBytes, bufferLengthInBytes)
            audioInputStream = AudioInputStream(line)
            audioInputStream = convertToAudioIStream(out, frameSizeInBytes)
            audioInputStream.reset()
            line.close()
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }

    @Throws(IOException::class)
    private fun getTargetDataLineForRecord(): TargetDataLine {
        val line: TargetDataLine
        val info = DataLine.Info(TargetDataLine::class.java, format)
        if (!AudioSystem.isLineSupported(info)) {
            throw IOException("Target data Line not supported")
        }
        line = AudioSystem.getLine(info) as TargetDataLine
        if (!line.isOpen) {
            line.open(format, line.bufferSize)
        }
        return line
    }

    @Throws(IOException::class)
    fun buildByteOutputStream(
        out: ByteArrayOutputStream,
        line: TargetDataLine,
        frameSizeInBytes: Int,
        bufferLengthInBytes: Int
    ) {
        val data = ByteArray(bufferLengthInBytes)
        var numBytesRead: Int

        line.start()
        while (record) {
            if (line.read(data, 0, bufferLengthInBytes).also { numBytesRead = it } == -1) {
                break
            }
            out.write(data, 0, numBytesRead)
        }
    }

    fun convertToAudioIStream(out: ByteArrayOutputStream, frameSizeInBytes: Int): AudioInputStream {
        val audioBytes = out.toByteArray()
        val inputStream = ByteArrayInputStream(audioBytes)
        val audioStream = AudioInputStream(inputStream, format, (audioBytes.size / frameSizeInBytes).toLong())
        val milliseconds = (audioStream.frameLength * 1000 / format.frameRate)
        val duration = milliseconds / 1000.0
        println("Recorded duration in seconds:$duration")
        return audioStream
    }
}