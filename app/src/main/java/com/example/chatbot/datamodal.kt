package com.example.chatbot
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

data class Message(
    val role: String,
    val content: String
)

data class ChatRequest(
    val model: String,
    val messages: List<Message>
)

data class ChatResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: Message
)

interface OpenRouterApi {
    @POST("chat/completions")
    fun chat(
        @Body request: ChatRequest,
        @Header("Authorization") token: String,
        @Header("HTTP-Referer") referer: String = "https://yourapp.com",
        @Header("X-Title") title: String = "MyChatApp"
    ): Call<ChatResponse>
}