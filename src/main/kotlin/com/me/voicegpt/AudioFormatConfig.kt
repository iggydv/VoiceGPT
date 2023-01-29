package com.me.voicegpt

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import javax.sound.sampled.AudioFormat

@Component
class AudioFormatConfig @Autowired constructor(private val properties: ApplicationProperties) {
    fun buildAudioFormatInstance(): AudioFormat {
        val encoding = properties.ENCODING
        val rate = properties.RATE
        val channels = properties.CHANNELS
        val sampleSize = properties.SAMPLE_SIZE
        val bigEndian = properties.BIG_ENDIAN
        return AudioFormat(encoding, rate, sampleSize, channels, sampleSize / 8 * channels, rate, bigEndian)
    }
}