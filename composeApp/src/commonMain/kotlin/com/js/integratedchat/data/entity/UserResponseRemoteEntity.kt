package com.js.integratedchat.data.entity

import com.js.integratedchat.domain.entity.UserEntity
import kotlinx.serialization.Serializable

@Serializable
data class UserResponseRemoteEntity(
    val id: String,
    val email: String,
    val name: String,
    val displayName: String?,
    val imageUrl: String?,
)

fun List<UserResponseRemoteEntity>.toRemote(): List<UserEntity> {
    return this.map{
        it.toRemote()
    }
}

fun UserResponseRemoteEntity.toRemote() = UserEntity(
    id = this.id,
    email = this.email,
    name = this.name,
    displayName = this.displayName,
    imageUrl = this.imageUrl
)