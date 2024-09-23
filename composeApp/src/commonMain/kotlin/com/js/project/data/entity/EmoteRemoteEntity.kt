package com.js.project.data.entity

import com.js.project.domain.entity.EmoteEntity
import kotlinx.serialization.Serializable


@Serializable
data class EmoteRemoteEntity(
    val emoteId: String,
    val positions: List<EmotePositionRemoteEntity>
)

fun List<EmoteRemoteEntity>.toDomain(): List<EmoteEntity> {
    return this.map{
        it.toDomain()
    }
}

fun EmoteRemoteEntity.toDomain() = EmoteEntity(
    emoteId = emoteId,
    url = "https://static-cdn.jtvnw.net/emoticons/v2/$emoteId/default/dark/1.0",
    positions = positions.toDomain()
)
