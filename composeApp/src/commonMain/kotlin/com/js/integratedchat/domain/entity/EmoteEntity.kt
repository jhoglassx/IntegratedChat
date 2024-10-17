package com.js.integratedchat.domain.entity

import kotlinx.serialization.Serializable


@Serializable
data class EmoteEntity(
    val emoteId: String,
    val url: String,
    val positions: List<EmotePositionEntity>
)

@Serializable
data class EmotePositionEntity(
    val start: Int,
    val end: Int,
)
