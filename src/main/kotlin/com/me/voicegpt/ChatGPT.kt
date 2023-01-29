package com.me.voicegpt

import org.json.JSONObject
import org.springframework.stereotype.Component
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

@Component
class ChatGPT {

    val apiEndpoint = "https://api.openai.com/v1/completions"
    val apiKey = System.getenv("CHAT_GPT_KEY")

    fun askChatGPT(voiceCommand: String) {
        // Send a request to the Chat GPT API to convert the voice command to text
        val response = sendPostRequest(apiEndpoint, apiKey, voiceCommand)
        val message = extractMessage(response)
        println("ChatGPT says: $message")
    }

    fun extractMessage(json: String): String {
        val jsonObject = JSONObject(json)
        // Extract the value of the "message" key
        return (jsonObject.getJSONArray("choices").get(0) as JSONObject).getString("text").toString().replace("\n", "")
    }

    fun sendPostRequest(apiEndpoint: String, apiKey: String, voiceCommand: String): String {
        val url = URL(apiEndpoint)
        val con = url.openConnection() as HttpURLConnection
        con.requestMethod = "POST"
        con.setRequestProperty("Content-Type", "application/json")
        con.setRequestProperty("Authorization", "Bearer $apiKey")

        val payload = """{"model": "text-davinci-003", "prompt": "$voiceCommand", "temperature": 0, "max_tokens": 64, "echo": false}"""

        con.doOutput = true
        val outputStream = con.outputStream
        outputStream.write(payload.toByteArray(Charsets.UTF_8))
        outputStream.close()

        val responseCode = con.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val inputStream = con.inputStream
            val reader = BufferedReader(InputStreamReader(inputStream))
            val response = StringBuffer()
            var line: String? = reader.readLine()
            while (line != null) {
                response.append(line)
                line = reader.readLine()
            }
            reader.close()
            return response.toString()
        } else {
            throw RuntimeException("Failed to send POST request: HTTP error code: $responseCode")
        }
    }

}