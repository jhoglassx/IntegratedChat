package com.js.integratedchat.data.entity

import com.js.integratedchat.domain.entity.UserEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserRemoteEntity(
    val id: String,
    val email: String,
    @SerialName("given_name")
    val name: String,
    @SerialName("name")
    val displayName: String?,
    @SerialName("picture")
    val imageUrl: String?,
)

fun List<UserRemoteEntity>.toRemote(): List<UserEntity> {
    return this.map{
        it.toRemote()
    }
}

fun UserRemoteEntity.toRemote() = UserEntity(
    id = this.id,
    email = this.email,
    name = this.name,
    displayName = this.displayName,
    imageUrl = this.imageUrl
)