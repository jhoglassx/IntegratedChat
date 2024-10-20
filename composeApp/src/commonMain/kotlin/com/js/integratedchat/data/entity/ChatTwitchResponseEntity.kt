package com.js.integratedchat.data.entity

import com.js.integratedchat.domain.entity.BadgeEntity
import com.js.integratedchat.domain.entity.ChatMessageEntity
import kotlinx.datetime.Instant
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class ChatTwitchResponseEntity(
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
    val channelName: String? = null,
    val isSubscriber: Boolean,
    val isModerator: Boolean,
    val isVIP: Boolean,
    val color: String?,
    val clientNonce: String?,
    val messageType: String?,
    val bits: Int?
)

fun ChatTwitchResponseEntity.toDomain() = ChatMessageEntity(
    id = id,
    userId = userId,
    userName = userName,
    displayName = displayName,
    timestamp = timestamp,
    message = message,
    badges = badges?.map { badge ->
        BadgeEntity(
            badgeType = badge.id,
            url = badge.imageUrl1x
        )
    },
    emotes = emotes?.toDomain(),
    source = source,
    channelId = channelId,
    channelName = channelName,
)