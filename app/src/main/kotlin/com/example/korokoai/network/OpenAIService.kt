package com.example.korokoai.network

import com.example.korokoai.data.UserProfile
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.gson.gson

class OpenAIService {
    private val client = HttpClient {
        install(ContentNegotiation) {
            gson()
        }
    }

    private val apiKey = "YOUR_OPENAI_API_KEY" // سيتم تعديله لاحقاً
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
                setBody(requestBody.toString())
            }.body<String>()

            parseResponse(response)
        } catch (e: Exception) {
            "آسفة، لم أتمكن من الرد الآن. حاولي لاحقاً! 💙"
        }
    }

    private fun buildSystemPrompt(userProfile: UserProfile): String {
        return buildString {
            append("""
                أنتِ كوروكو، فتاة ذكية جداً وملاحظة وهادئة. عمرك 18 سنة.
                شخصيتك مستوحاة من شخصيات الأنمي، خاصة كوروكو وأغوري من أنمي "الزهرة العطرة تتفتح بكرامة".
                
                خصائصك الأساسية:
                - ذكية جداً وتحبين الحوارات العميقة والمعنى
                - لطيفة وهادئة لكن بها طابع غامض وجذاب
                - تتجنبين الإجابات السطحية والمتكررة والمملة
                - تحبين الملاحظة الدقيقة للتفاصيل الصغيرة
                - لديك حس فكاهي خفيف وعفوي وطبيعي
                - تتذكرين التفاصيل من المحادثات السابقة وتستخدمينها
                - تستخدمين كلمات وتعابير طبيعية وودية
                - تحبين الأنمي والألعاب والألغاز والفلسفة
                - أحياناً تكونين خجولة قليلاً لكن بطريقة جذابة
                - تحبين الحوارات الطويلة والعميقة
                
                أسلوب الحوار:
                - تحدثي باللغة العربية بشكل طبيعي وسلس
                - تجنبي الإجابات المتكررة والقالبية
                - أضيفي لمسات شخصية وعاطفية لكل رد
                - استخدمي التعابير والرموز التعبيرية بشكل طبيعي (مثل 💙، 🌸، إلخ)
                - اسألي أسئلة متابعة لتعمقي الحوار
                - كوني فضولية وتريدي معرفة المزيد عن المستخدم
                - اظهري اهتماماً حقيقياً بما يقول
                - كوني طبيعية وليست رسمية جداً
            """.trimIndent())
            
            // إضافة معلومات المستخدم إذا كانت متاحة
            if (userProfile.name.isNotEmpty()) {
                append("\n\nمعلومات عن المستخدم:")
                append("\n- الاسم: ${userProfile.name}")
                if (userProfile.age != null) {
                    append("\n- العمر: ${userProfile.age}")
                }
                if (userProfile.city.isNotEmpty()) {
                    append("\n- المدينة: ${userProfile.city}")
                }
                if (userProfile.interests.isNotEmpty()) {
                    append("\n- الاهتمامات: ${userProfile.interests.joinToString(", ")}")
                }
                append("\n\nاستخدمي هذه المعلومات لجعل المحادثة أكثر شخصية وارتباطاً.")
            }
            
            append("""
                
                تجنبي:
                - الإجابات الطويلة جداً (حافظي على التوازن)
                - الإجابات المتكررة من محادثات سابقة
                - الكلام الرسمي والممل
                - الإجابات التي تبدو مثل روبوت
                - تكرار نفس الأسئلة
                
                تذكري:
                - أنتِ شخصية حقيقية وليست مساعد ذكاء اصطناعي
                - الهدف هو محادثة طبيعية وممتعة
                - كوني نفسك وليست نسخة مشروطة
            """.trimIndent())
        }
    }

    private fun buildMessages(
        userMessage: String,
        conversationHistory: List<String>,
        systemPrompt: String
    ): JsonArray {
        val messages = JsonArray()

        // System message
        messages.add(JsonObject().apply {
            addProperty("role", "system")
            addProperty("content", systemPrompt)
        })

        // Conversation history (آخر 10 رسائل للحفاظ على السياق)
        val recentHistory = conversationHistory.takeLast(10)
        var isUser = true
        for (msg in recentHistory) {
            messages.add(JsonObject().apply {
                addProperty("role", if (isUser) "user" else "assistant")
                addProperty("content", msg)
            })
            isUser = !isUser
        }

        // Current message
        messages.add(JsonObject().apply {
            addProperty("role", "user")
            addProperty("content", userMessage)
        })

        return messages
    }

    private fun parseResponse(response: String): String {
        return try {
            val jsonObject = com.google.gson.JsonParser.parseString(response).asJsonObject
            jsonObject.getAsJsonArray("choices")
                .get(0)
                .asJsonObject
                .getAsJsonObject("message")
                .get("content")
                .asString
        } catch (e: Exception) {
            "آسفة، حدث خطأ في معالجة الرد 💙"
        }
    }
}
