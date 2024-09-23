package com.js.project.domain.entity

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
    val source: String = "Twitch",
    val channelId: String? = null,
    val channelName: String? = null
)