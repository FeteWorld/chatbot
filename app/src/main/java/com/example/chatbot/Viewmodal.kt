package com.example.chatbot

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlinx.coroutines.CoroutineScope

class Viewmodal : ViewModel() {
    private val _messages = MutableLiveData<MutableList<ChatMessage>>(mutableListOf())
    val messages: LiveData<MutableList<ChatMessage>> = _messages

    fun sendUserMessage(context: Context, message: String) {
        _messages.value?.add(ChatMessage(message, true))
        _messages.postValue(_messages.value)

        val personalInfo = loadPersonalData(context)
        val prompt = """
        You are a personal assistant that knows the following information about the user:

        ---
        $personalInfo
        ---

        Instructions:
        1. If the user is just greeting (like "hi", "hello", "hey"), reply casually and DO NOT mention any personal details.
        2. If the user asks a specific question about the user's details (like name, profession, skills, hobbies, contact, etc.), reply accurately using the information provided.
        3. Always reply naturally, clearly, and concisely.

        User's message: "$message"
    """.trimIndent()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://openrouter.ai/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(OpenRouterApi::class.java)

        val request = ChatRequest(
            model = "mistralai/mistral-7b-instruct", // or gpt-3.5, gpt-4
            messages = listOf(Message("user", prompt))
        )

        val apiKey = "Bearer ${BuildConfig.OPENROUTER_API_KEY}"

        api.chat(request, apiKey)
            .enqueue(object : Callback<ChatResponse> {
                override fun onResponse(call: Call<ChatResponse>, response: Response<ChatResponse>) {
                    val reply = response.body()?.choices?.firstOrNull()?.message?.content
                    _messages.value?.add(ChatMessage(reply ?: "No response", false))
                    _messages.postValue(_messages.value)
                }

                override fun onFailure(call: Call<ChatResponse>, t: Throwable) {
                    _messages.value?.add(ChatMessage("Failure: ${t.message}", false))
                    _messages.postValue(_messages.value)
                }
            })
    }

    fun loadPersonalData(context: Context): String {
        val prefs = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        return prefs.getString("userInput", "") ?: ""
//        return context.assets.open("mydetails").bufferedReader().use { it.readText() }
    }
}