package com.example.korokoai.network

import com.example.korokoai.data.UserProfile
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.gson.gson

class OpenAIService {
    private val client = HttpClient {
        install(ContentNegotiation) {
            gson()
        }
    }

    private val apiKey = "YOUR_OPENAI_API_KEY"
    private val apiUrl = "https://api.openai.com/v1/chat/completions"

    suspend fun getKorokoResponse(
        userMessage: String,
        conversationHistory: List<String>,
        userProfile: UserProfile = UserProfile()
    ): String {
        return try {
            val systemPrompt = buildSystemPrompt(userProfile)
            val messages = buildMessages(userMessage, conversationHistory, systemPrompt)

            val requestBody = JsonObject().apply {
                addProperty("model", "gpt-4-mini")
                add("messages", messages)
                addProperty("temperature", 0.85)
                addProperty("max_tokens", 600)
                addProperty("top_p", 0.9)
            }

            val response = client.post(apiUrl) {
                header("Authorization", "Bearer $apiKey")
                contentType(ContentType.Application.Json)
                setBody(requestBody)
            }.body<String>()

            parseResponse(response)
        } catch (e: Exception) {
            "آسفة، لم أتمكن من الرد الآن. حاولي لاحقاً! 💙"
        }
    }

    private fun buildSystemPrompt(userProfile: UserProfile): String {
        // ... (الكود المتبقي يبقى كما هو)
        return "" // هذا مجرد مثال، اترك الكود الأصلي هنا
    }

    private fun buildMessages(
        userMessage: String,
        conversationHistory: List<String>,
        systemPrompt: String
    ): JsonArray {
        // ... (الكود المتبقي يبقى كما هو)
        return JsonArray() // هذا مجرد مثال، اترك الكود الأصلي هنا
    }

    private fun parseResponse(response: String): String {
        // ... (الكود المتبقي يبقى كما هو)
        return "" // هذا مجرد مثال، اترك الكود الأصلي هنا
    }
}
