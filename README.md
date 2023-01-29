# VoiceGPT

A small wrapper application that allows chatGPT voice commands


## Prerequisites

- Google API key + enabled speech-to-text API
  - `GOOGLE_APPLICATION_CREDENTIALS`
- ChatGPT API key
  - `CHAT_GPT_KEY`


## API

```shell
curl -X GET "localhost:8080/ask/start"
# speak into your microphone - ask a question (really go for it)

curl -X GET "localhost:8080/ask/stop"
# check the console output
```
