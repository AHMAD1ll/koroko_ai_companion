package com.example.korokoai.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * مدير المحادثات والذاكرة
 * يتولى إدارة سجل المحادثات والمعلومات المهمة عن المستخدم
 */
class ConversationManager {
    
    // سجل المحادثات الكامل
    private val _conversationHistory = MutableStateFlow<List<String>>(emptyList())
    val conversationHistory: StateFlow<List<String>> = _conversationHistory
    
    // معلومات المستخدم المستخرجة
    private val _userProfile = MutableStateFlow<UserProfile>(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile
    
    // الكلمات المفتاحية والاهتمامات
    private val _extractedKeywords = MutableStateFlow<Set<String>>(emptySet())
    val extractedKeywords: StateFlow<Set<String>> = _extractedKeywords
    
    /**
     * إضافة رسالة جديدة إلى السجل
     */
    fun addMessage(message: String) {
        val currentHistory = _conversationHistory.value.toMutableList()
        currentHistory.add(message)
        _conversationHistory.value = currentHistory
        
        // استخراج المعلومات من الرسالة
        extractUserInfo(message)
    }
    
    /**
     * الحصول على آخر N رسالة
     */
    fun getRecentMessages(count: Int = 10): List<String> {
        return _conversationHistory.value.takeLast(count)
    }
    
    /**
     * الحصول على السياق الكامل للمحادثة
     */
    fun getFullContext(): String {
        return _conversationHistory.value.joinToString("\n")
    }
    
    /**
     * استخراج معلومات المستخدم من الرسائل
     */
    private fun extractUserInfo(message: String) {
        val currentProfile = _userProfile.value.copy()
        
        // استخراج الاسم (مثال: "اسمي أحمد")
        val namePattern = Regex("اسمي\\s+([\\u0600-\\u06FF]+)", RegexOption.IGNORE_CASE)
        namePattern.find(message)?.let {
            currentProfile.name = it.groupValues[1]
        }
        
        // استخراج العمر (مثال: "عمري 25")
        val agePattern = Regex("عمري\\s+(\\d+)", RegexOption.IGNORE_CASE)
        agePattern.find(message)?.let {
            currentProfile.age = it.groupValues[1].toIntOrNull()
        }
        
        // استخراج المدينة (مثال: "أنا من بغداد")
        val cityPattern = Regex("من\\s+([\\u0600-\\u06FF]+)", RegexOption.IGNORE_CASE)
        cityPattern.find(message)?.let {
            currentProfile.city = it.groupValues[1]
        }
        
        // استخراج الاهتمامات
        val interests = extractInterests(message)
        if (interests.isNotEmpty()) {
            currentProfile.interests = (currentProfile.interests + interests).distinct()
        }
        
        _userProfile.value = currentProfile
        
        // تحديث الكلمات المفتاحية
        updateKeywords(message)
    }
    
    /**
     * استخراج الاهتمامات من الرسالة
     */
    private fun extractInterests(message: String): List<String> {
        val interestKeywords = mapOf(
            "أنمي" to listOf("أنمي", "anime"),
            "ألعاب" to listOf("ألعاب", "games", "gaming"),
            "قراءة" to listOf("قراءة", "كتب", "reading", "books"),
            "رياضة" to listOf("رياضة", "كرة", "sports"),
            "سفر" to listOf("سفر", "سياحة", "travel"),
            "موسيقى" to listOf("موسيقى", "أغاني", "music"),
            "فن" to listOf("فن", "رسم", "art"),
            "تقنية" to listOf("تقنية", "برمجة", "tech", "programming"),
            "طبخ" to listOf("طبخ", "طعام", "cooking", "food"),
            "أفلام" to listOf("أفلام", "سينما", "movies")
        )
        
        val foundInterests = mutableListOf<String>()
        for ((interest, keywords) in interestKeywords) {
            for (keyword in keywords) {
                if (message.contains(keyword, ignoreCase = true)) {
                    foundInterests.add(interest)
                    break
                }
            }
        }
        
        return foundInterests
    }
    
    /**
     * تحديث الكلمات المفتاحية
     */
    private fun updateKeywords(message: String) {
        val words = message.split("\\s+".toRegex())
            .filter { it.length > 3 }
            .map { it.lowercase() }
            .toSet()
        
        val currentKeywords = _extractedKeywords.value.toMutableSet()
        currentKeywords.addAll(words)
        _extractedKeywords.value = currentKeywords
    }
    
    /**
     * مسح السجل (إعادة تعيين المحادثة)
     */
    fun clearHistory() {
        _conversationHistory.value = emptyList()
        _userProfile.value = UserProfile()
        _extractedKeywords.value = emptySet()
    }
    
    /**
     * الحصول على ملخص المحادثة
     */
    fun getSummary(): String {
        val profile = _userProfile.value
        val messageCount = _conversationHistory.value.size
        
        return buildString {
            append("📊 ملخص المحادثة:\n")
            append("عدد الرسائل: $messageCount\n")
            if (profile.name.isNotEmpty()) {
                append("الاسم: ${profile.name}\n")
            }
            if (profile.age != null) {
                append("العمر: ${profile.age}\n")
            }
            if (profile.city.isNotEmpty()) {
                append("المدينة: ${profile.city}\n")
            }
            if (profile.interests.isNotEmpty()) {
                append("الاهتمامات: ${profile.interests.joinToString(", ")}\n")
            }
        }
    }
}

/**
 * ملف تعريف المستخدم
 */
data class UserProfile(
    var name: String = "",
    var age: Int? = null,
    var city: String = "",
    var interests: List<String> = emptyList(),
    var favoriteTopics: List<String> = emptyList(),
    var conversationStyle: String = "طبيعي"
)
