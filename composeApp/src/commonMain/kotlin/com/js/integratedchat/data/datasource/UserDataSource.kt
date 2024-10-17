package com.js.integratedchat.data.datasource

import com.js.integratedchat.data.entity.UserResponseRemoteEntity
import kotlinx.coroutines.flow.Flow

interface UserDataSource {

    suspend fun fetchUserGoogle(
        userInfoUrl: String,
        accessToken: String,
        clientId: String
    ): Flow<UserResponseRemoteEntity>

    suspend fun fetchUserTwitch(
        userInfoUrl: String,
        accessToken: String,
        clientId: String
    ): Flow<UserResponseRemoteEntity>
}
