package com.js.project.data.repository

import com.js.project.domain.entity.UserEntity
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

