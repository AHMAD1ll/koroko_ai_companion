package com.example.korokoai.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.korokoai.data.ConversationManager
import com.example.korokoai.network.HuggingFaceService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<String>>(emptyList())
    val messages: StateFlow<List<String>> = _messages

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val conversationManager = ConversationManager()
    private val aiService = HuggingFaceService()

    init {
        // رسالة ترحيب أولية
        _messages.value = listOf("كوروكو: مرحباً! أنا كوروكو 🌸 كيف حالك؟")
        conversationManager.addMessage("كوروكو: مرحباً! أنا كوروكو 🌸 كيف حالك؟")
    }

    fun sendMessage(userMessage: String) {
        // إضافة رسالة المستخدم
        val userMsg = "أنت: $userMessage"
        _messages.value = _messages.value + userMsg
        conversationManager.addMessage(userMessage)

        // تعيين حالة التحميل
        _isLoading.value = true

        // الحصول على رد من كوروكو
        viewModelScope.launch {
            try {
                // الحصول على السياق من مدير المحادثات
                val recentMessages = conversationManager.getRecentMessages(10)
                val userProfile = conversationManager.userProfile.value
                
                val response = aiService.getKorokoResponse(
                    userMessage,
                    recentMessages,
                    userProfile
                )
                
                val korokoMsg = "كوروكو: $response"
                _messages.value = _messages.value + korokoMsg
                conversationManager.addMessage(response)
            } catch (e: Exception) {
                val errorMsg = "كوروكو: آسفة، حدث خطأ ما. حاولي لاحقاً... 💙"
                _messages.value = _messages.value + errorMsg
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * الحصول على ملخص المحادثة
     */
    fun getConversationSummary(): String {
        return conversationManager.getSummary()
    }

    /**
     * مسح المحادثة
     */
    fun clearConversation() {
        conversationManager.clearHistory()
        _messages.value = listOf("كوروكو: مرحباً! أنا كوروكو 🌸 كيف حالك؟")
    }

    /**
     * الحصول على معلومات المستخدم المستخرجة
     */
    fun getUserProfile() = conversationManager.userProfile
}
