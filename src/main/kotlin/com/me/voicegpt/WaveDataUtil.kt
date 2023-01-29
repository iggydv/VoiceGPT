package com.me.voicegpt

import java.io.File
import javax.sound.sampled.AudioFileFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem

class WaveDataUtil {
    fun saveToFile(name: String?, fileType: AudioFileFormat.Type?, audioInputStream: AudioInputStream?): String {
        println("Saving...")
        if (null == name || null == fileType || audioInputStream == null) {
            return ""
        }
        var myFile = File(name + "." + fileType.extension)
        try {
            audioInputStream.reset()
        } catch (e: Exception) {
            return ""
        }
        val i = 0
        while (myFile.exists()) {
            val temp = "" + i + myFile.name
            myFile = File(temp)
        }
        try {
            AudioSystem.write(audioInputStream, fileType, myFile)
        } catch (ex: Exception) {
            return ""
        }
        println("Saved " + myFile.absolutePath)
        return myFile.absolutePath
    }
}