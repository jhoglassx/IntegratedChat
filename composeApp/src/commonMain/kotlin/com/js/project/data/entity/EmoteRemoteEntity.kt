package com.js.project.data.entity

import com.js.project.domain.entity.EmoteEntity
import kotlinx.serialization.Serializable


@Serializable
data class EmoteRemoteEntity(
    val start: Int,
    val end: Int
)


fun List<EmoteRemoteEntity>.toDomain(): List<EmoteEntity> {
    return this.map{
        it.toDomain()
    }
}

fun EmoteRemoteEntity.toDomain() = EmoteEntity(
    end = end,
    start = start
)