package com.js.integratedchat.data.repository

import com.js.integratedchat.domain.entity.UserEntity
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun fetchUserGoogle(
        userInfoUrl: String,
        accessToken: String,
        clientId: String
    ): Flow<UserEntity>

    suspend fun fetchUserTwitch(
        userInfoUrl: String,
        accessToken: String,
        clientId: String
    ): Flow<UserEntity>
}

