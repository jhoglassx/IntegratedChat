package com.js.integratedchat.data.entity

import com.js.integratedchat.domain.entity.EmoteEntity
import kotlinx.serialization.Serializable


@Serializable
data class EmoteRemoteEntity(
    val emoteId: String,
    val imgUrl: String,
    val positions: List<EmotePositionRemoteEntity>
)

fun List<EmoteRemoteEntity>.toDomain(): List<EmoteEntity> {
    return this.map{
        it.toDomain()
    }
}

fun EmoteRemoteEntity.toDomain() = EmoteEntity(
    emoteId = emoteId,
    url = imgUrl,
    positions = positions.toDomain()
)
