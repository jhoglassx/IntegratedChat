package com.js.project.domain.entity


data class ProcessedMessage(
    val text: String? = null,
    val imageUrl: String? = null
)

fun ChatMessageEntity.processTwitchMessage(): List<ProcessedMessage> {
    val parts = mutableListOf<ProcessedMessage>()
    var currentIndex = 0

    val semEmojis = message.removeEmojis()

    val sortedEmotes = emotes?.flatMap { emote ->
        emote.positions.map { position -> emote to position }
    }?.sortedBy { it.second.start } ?: emptyList()

    for ((emote, position) in sortedEmotes) {
        if (currentIndex < position.start) {
            parts.add(ProcessedMessage(text = semEmojis.substring(currentIndex, position.start)))
        }
        parts.add(ProcessedMessage(imageUrl = emote.url))
        currentIndex = position.end + 1
    }

    if (currentIndex < semEmojis.length) {
        parts.add(ProcessedMessage(text = semEmojis.substring(currentIndex)))
    }

    return parts
}

fun String.removeEmojis(): String {
    // Regex pattern to match :nome-do-emoji:
    val emojiPattern = ":\\w+(-\\w+)*:".toRegex()
    return this.replace(emojiPattern, "")
}