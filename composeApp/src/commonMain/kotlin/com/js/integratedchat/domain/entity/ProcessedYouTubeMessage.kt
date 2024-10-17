package com.js.integratedchat.domain.entity

val emoteMap = mapOf(
    ":emote1:" to "https://example.com/emote1.png",
    ":emote2:" to "https://example.com/emote2.png"
)

fun ChatMessageEntity.processYouTubeMessage(): List<ProcessedMessage> {
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