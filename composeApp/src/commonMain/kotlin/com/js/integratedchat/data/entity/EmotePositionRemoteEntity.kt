package com.js.integratedchat.data.entity

import com.js.integratedchat.domain.entity.EmotePositionEntity
import kotlinx.serialization.Serializable


@Serializable
data class EmotePositionRemoteEntity(
    val start: Int,
    val end: Int,
)

fun List<EmotePositionRemoteEntity>.toDomain() = this.map {
    it.toDomain()
}

fun EmotePositionRemoteEntity.toDomain() = EmotePositionEntity(
    start = start,
    end = end
)