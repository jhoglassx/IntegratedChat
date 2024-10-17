package com.js.integratedchat.data.entity

import com.js.integratedchat.domain.entity.UserEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val data: List<UserTwitchRemoteEntity>
)

@Serializable
data class UserTwitchRemoteEntity(
    val id: String,
    val login: String,
    @SerialName("display_name")
    val displayName: String,
    val type: String,
    @SerialName("broadcaster_type")
    val broadcasterType: String,
    val description: String,
    @SerialName("profile_image_url")
    val profileImageUrl: String,
    @SerialName("offline_image_url")
    val offlineImageUrl: String,
    @SerialName("view_count")
    val viewCount: Int,
    val email: String,
    @SerialName("created_at")
    val createdAt: String
)

fun UserTwitchRemoteEntity.toRemote() = UserEntity(
    id = this.id,
    email = this.email,
    name = this.login,
    displayName = this.displayName,
    imageUrl = this.profileImageUrl
)