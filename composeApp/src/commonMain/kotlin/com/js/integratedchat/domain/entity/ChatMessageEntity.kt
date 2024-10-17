package com.js.integratedchat.domain.entity

import com.js.integratedchat.data.datasource.SourceEnum
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
    val badges: List<BadgeEntity>? = null,
    val emotes: List<EmoteEntity>? = null,
    val source: SourceEnum,
    val channelId: String? = null,
    val channelName: String? = null
)