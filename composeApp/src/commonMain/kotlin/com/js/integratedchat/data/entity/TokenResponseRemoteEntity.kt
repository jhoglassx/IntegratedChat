package com.js.integratedchat.data.entity

import com.js.integratedchat.domain.entity.TokenEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TokenResponseRemoteEntity(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("refresh_token")
    val refreshToken: String? = null,
    @SerialName("expires_in")
    val expiresIn: Int,
    @SerialName("token_type")
    val tokenType: String
)

fun TokenResponseRemoteEntity.toRemote() = TokenEntity(
    accessToken = accessToken,
    refreshToken = refreshToken,
    expiresIn = expiresIn,
    tokenType = tokenType
)