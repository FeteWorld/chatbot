package com.example.chatbot
import retrofit2.Call
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import retrofit2.Callback
import retrofit2.Response

class repository {
    private val api: OpenRouterApi

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://openrouter.ai/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(OpenRouterApi::class.java)
    }

    fun sendMessageToBot(
        userMsg: String,
        onResult: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val request = ChatRequest(
            model = "mistralai/mistral-7b-instruct",
            messages = listOf(Message("user", userMsg))
        )

        api.chat(request, "Bearer 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").enqueue(object : Callback<ChatResponse> {
            override fun onResponse(call: Call<ChatResponse>, response: Response<ChatResponse>) {
                if (response.isSuccessful) {
                    val reply = response.body()?.choices?.firstOrNull()?.message?.content
                    if (!reply.isNullOrBlank()) {
                        onResult(reply)
                    } else {
                        onError("Empty response")
                    }
                } else {
                    onError("API Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ChatResponse>, t: Throwable) {
                onError("Failure: ${t.message}")
            }
        })
    }
}