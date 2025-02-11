package com.example.recommondationsys.data.model


data class ChatMessage(
    val text: String,
    val type: MessageType,
    val recommendations: List<Restaurant> = emptyList()
)

enum class MessageType {
    USER, SYSTEM, RECOMMENDATION
}
