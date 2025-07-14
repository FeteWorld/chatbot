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
    private val repository = repository()
    private val _messages = MutableLiveData<MutableList<ChatMessage>>(mutableListOf())
    val messages: LiveData<MutableList<ChatMessage>> = _messages

//    fun sendUserMessage1(userText: String) {
//        val updatedList = _messages.value ?: mutableListOf()
//        updatedList.add(ChatMessage(userText, true))
//        _messages.value = updatedList
//
//        repository.sendMessageToBot(
//            userText,
//            onResult = { reply ->
//                val newList = _messages.value ?: mutableListOf()
//                newList.add(ChatMessage(reply, false))
//                _messages.postValue(newList)
//            },
//            onError = { error ->
//                val newList = _messages.value ?: mutableListOf()
//                newList.add(ChatMessage(error, false))
//                _messages.postValue(newList)
//            }
//        )
//    }

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

        api.chat(request, "Bearer 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")
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