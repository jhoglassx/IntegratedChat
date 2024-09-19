package com.js.project.domain.entity

import com.js.project.data.entity.ChatMessageEntityRemote
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class ChatMessageEntity(
    val id: String = "",
    val userId: String? = null,
    val userName: String? = null,
    val displayName: String = "",
    @Contextual
    val timestamp: Instant? = null,
    val message: String = "",
    val badges: Map<String, String> = emptyMap(),
    val emotes: Map<String, List<EmoteEntity>> = emptyMap(),
    val source: String = "Twitch",
    val channelId: String? = null,
    val channelName: String? = null
)

fun List<ChatMessageEntity>.toRemote(): List<ChatMessageEntityRemote> {
    return this.map{
        it.toRemote()
    }
}

fun ChatMessageEntity.toRemote() = ChatMessageEntityRemote(
    id = id,
    userId = userId,
    userName = userName,
    displayName = displayName,
    timestamp = timestamp,
    message = message,
    badges = badges,
    emotes = emotes.mapValues { entry ->
        entry.value.map { it.toRemote()}
    },
    source = source,
    channelId = channelId,
    channelName = channelName
)