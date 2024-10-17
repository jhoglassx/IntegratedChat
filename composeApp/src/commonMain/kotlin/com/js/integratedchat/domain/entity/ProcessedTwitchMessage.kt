package com.js.integratedchat.domain.entity


data class ProcessedMessage(
    val text: String? = null,
    val imageUrl: String? = null
)

fun ChatMessageEntity.processMessage(): List<ProcessedMessage> {
    val parts = mutableListOf<ProcessedMessage>()
    var currentIndex = 0

    val sortedEmotes = emotes?.flatMap { emote ->
        emote.positions.map { position -> emote to position }
    }?.sortedBy { it.second.start } ?: emptyList()

    for ((emote, position) in sortedEmotes) {
        if (currentIndex < position.start) {
            parts.add(ProcessedMessage(text = message.substring(currentIndex, position.start)))
        }
        parts.add(ProcessedMessage(imageUrl = emote.url))
        currentIndex = position.end + 1
    }

    if (currentIndex < message.length) {
        parts.add(ProcessedMessage(text = message.substring(currentIndex)))
    }

    return parts
}