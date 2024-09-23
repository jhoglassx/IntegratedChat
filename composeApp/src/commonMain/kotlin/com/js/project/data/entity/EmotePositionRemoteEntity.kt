package com.js.project.data.entity

import com.js.project.domain.entity.EmotePositionEntity
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