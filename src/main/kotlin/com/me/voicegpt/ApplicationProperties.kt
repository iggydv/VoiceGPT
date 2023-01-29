package com.me.voicegpt

import org.springframework.context.annotation.Configuration
import javax.sound.sampled.AudioFormat

@Configuration
class ApplicationProperties {
    val ENCODING = AudioFormat.Encoding.PCM_SIGNED
    val RATE = 44100.0f
    val CHANNELS = 1
    val SAMPLE_SIZE = 16
    val BIG_ENDIAN = true

    fun getEncoding(): AudioFormat.Encoding {
        return ENCODING
    }

    fun getRate(): Float {
        return RATE
    }

    fun getChannels(): Int {
        return CHANNELS
    }

    fun getSampleSize(): Int {
        return SAMPLE_SIZE
    }

    fun getBigEndian(): Boolean {
        return BIG_ENDIAN
    }
}

