package com.js.project.domain.entity

import com.js.project.data.entity.EmoteRemoteEntity
import kotlinx.serialization.Serializable


@Serializable
data class EmoteEntity(
    val start: Int,
    val end: Int
)


fun List<EmoteEntity>.toRemote(): List<EmoteRemoteEntity> {
    return this.map{
        it.toRemote()
    }
}

fun EmoteEntity.toRemote() = EmoteRemoteEntity(
    start = start,
    end = end
)