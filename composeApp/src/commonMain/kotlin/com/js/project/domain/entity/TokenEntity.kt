package com.js.project.domain.entity

import com.js.project.data.entity.TokenResponseRemoteEntity
import kotlinx.serialization.Serializable

@Serializable
data class TokenEntity(
    val accessToken: String,
    val refreshToken: String?,
    val expiresIn: Int,
    val tokenType: String
)

fun TokenEntity.toRemote() = TokenResponseRemoteEntity(
    accessToken = accessToken,
    refreshToken = refreshToken,
    expiresIn = expiresIn,
    tokenType = tokenType
)