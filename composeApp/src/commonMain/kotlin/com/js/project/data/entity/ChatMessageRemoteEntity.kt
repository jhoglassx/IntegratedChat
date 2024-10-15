package com.js.project.data.entity

import com.js.project.data.datasource.SourceEnum
import com.js.project.domain.entity.BadgeEntity
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
    val badges: List<BadgeResponse>? = listOf(),
    val emotes: List<EmoteRemoteEntity>? = listOf(),
    val source: SourceEnum,
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
    badges = badges?.map { badge ->
        BadgeEntity(
            badgeType = badge.id,
            url = badge.image_url_1x
        )
    },
    emotes = emotes?.toDomain(),
    source = source,
    channelId = channelId,
    channelName = channelName
)