package com.js.integratedchat.domain.entity

import com.js.integratedchat.data.entity.UserRemoteEntity
import kotlinx.serialization.Serializable

@Serializable
data class UserEntity(
    val id: String,
    val email: String,
    val name: String,
    val displayName: String?,
    val imageUrl: String?,
)

fun List<UserEntity>.toRemote(): List<UserRemoteEntity> {
    return this.map{
        it.toRemote()
    }
}

fun UserEntity.toRemote() = UserRemoteEntity(
    id = this.id,
    email = this.email,
    name = this.name,
    displayName = this.displayName,
    imageUrl = this.imageUrl
)