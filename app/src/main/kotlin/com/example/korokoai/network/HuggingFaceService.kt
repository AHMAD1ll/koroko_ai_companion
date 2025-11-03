package com.example.korokoai.network

import com.example.korokoai.data.UserProfile
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

class HuggingFaceService {
    private val client = HttpClient {
        install(ContentNegotiation) {
            gson()
        }
    }

    private val apiUrl = "https://api-inference.huggingface.co/models/mistralai/Mistral-7B-Instruct-v0.1"
    private val apiKey = ""

    suspend fun getKorokoResponse(
        userMessage: String,
        conversationHistory: List<String>,
        userProfile: UserProfile = UserProfile()
    ): String {
        return try {
            val systemPrompt = buildSystemPrompt(userProfile)
            val prompt = buildPrompt(userMessage, conversationHistory, systemPrompt)

            val requestBody = JsonObject().apply {
                addProperty("inputs", prompt)
                add("parameters", JsonObject().apply {
                    addProperty("max_new_tokens", 300)
                    addProperty("temperature", 0.85)
                    addProperty("top_p", 0.9)
                    addProperty("do_sample", true)
                })
            }

            val response = client.post(apiUrl) {
                if (apiKey.isNotEmpty()) {
                    header("Authorization", "Bearer $apiKey")
                }
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

    private fun buildPrompt(
        userMessage: String,
        conversationHistory: List<String>,
        systemPrompt: String
    ): String {
        // ... (الكود المتبقي يبقى كما هو)
        return "" // هذا مجرد مثال، اترك الكود الأصلي هنا
    }

    private fun parseResponse(response: String): String {
        // ... (الكود المتبقي يبقى كما هو)
        return "" // هذا مجرد مثال، اترك الكود الأصلي هنا
    }
}
