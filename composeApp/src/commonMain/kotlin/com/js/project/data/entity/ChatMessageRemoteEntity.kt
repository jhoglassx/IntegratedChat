package com.js.project.data.entity

import com.js.project.domain.entity.ChatMessageEntity
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class ChatMessageEntityRemote(
    val id: String = "",
    val userId: String? = null,
    val userName: String? = null,
    val displayName: String = "",
    @Contextual
    val timestamp: Instant? = null,
    val message: String = "",
    val badges: Map<String, String> = emptyMap(),
    val emotes: Map<String, List<EmoteRemoteEntity>> = emptyMap(),
    val source: String = "Twitch",
    val channelId: String? = null,
    val channelName: String? = null
)

fun List<ChatMessageEntityRemote>.toDomain(): List<ChatMessageEntity> {
    return this.map{
        it.toDomain()
    }
}

fun ChatMessageEntityRemote.toDomain() = ChatMessageEntity(
    id = id,
    userId = userId,
    userName = userName,
    displayName = displayName,
    timestamp = timestamp,
    message = message,
    badges = badges,
    emotes = emotes.mapValues { entry ->
        entry.value.map { it.toDomain() }
    },
    source = source,
    channelId = channelId,
    channelName = channelName
)