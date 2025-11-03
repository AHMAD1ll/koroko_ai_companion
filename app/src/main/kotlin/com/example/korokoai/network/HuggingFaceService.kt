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

/**
 * خدمة Hugging Face للحصول على ردود ذكية مجانية
 * تستخدم نموذج Mistral-7B الذي يوفر جودة عالية بدون تكاليف
 */
class HuggingFaceService {
    private val client = HttpClient {
        install(ContentNegotiation) {
            gson()
        }
    }

    // API مجاني من Hugging Face (لا يحتاج مفتاح)
    private val apiUrl = "https://api-inference.huggingface.co/models/mistralai/Mistral-7B-Instruct-v0.1"
    
    // يمكنك استخدام مفتاح مجاني من Hugging Face إذا أردت (اختياري)
    private val apiKey = ""  // اتركه فارغ للاستخدام المجاني

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
                setBody(requestBody.toString())
            }.body<String>()

            parseResponse(response)
        } catch (e: Exception) {
            // إذا فشل الاتصال، نرجع رد افتراضي
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
                - استخدمي التعابير والرموز التعبيرية بشكل طبيعي
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
        }
    }

    private fun buildPrompt(
        userMessage: String,
        conversationHistory: List<String>,
        systemPrompt: String
    ): String {
        return buildString {
            append("[INST] ")
            append(systemPrompt)
            append("\n\n")
            
            // إضافة آخر 5 رسائل من السياق
            val recentHistory = conversationHistory.takeLast(5)
            for (msg in recentHistory) {
                append(msg)
                append("\n")
            }
            
            append("\nالمستخدم: ")
            append(userMessage)
            append("\n\nكوروكو: [/INST]")
        }
    }

    private fun parseResponse(response: String): String {
        return try {
            val jsonArray = com.google.gson.JsonParser.parseString(response).asJsonArray
            if (jsonArray.size() > 0) {
                val firstElement = jsonArray.get(0).asJsonObject
                val generatedText = firstElement.get("generated_text").asString
                
                // استخراج الرد من النص المولد
                val parts = generatedText.split("[/INST]")
                if (parts.size > 1) {
                    parts[1].trim()
                        .replace("كوروكو:", "")
                        .trim()
                } else {
                    generatedText.trim()
                        .replace("كوروكو:", "")
                        .trim()
                }
            } else {
                "آسفة، لم أتمكن من الرد الآن 💙"
            }
        } catch (e: Exception) {
            "آسفة، حدث خطأ في معالجة الرد 💙"
        }
    }
}
