package com.js.integratedchat.data.datasource

import com.js.integratedchat.data.entity.UserGoogleRemoteEntity
import com.js.integratedchat.data.entity.UserRemoteEntity
import com.js.integratedchat.data.entity.UserTwitchRemoteEntity
import kotlinx.coroutines.flow.Flow

interface UserDataSource {

    suspend fun fetchUserGoogle(
        userInfoUrl: String,
        accessToken: String,
        clientId: String
    ): Flow<UserGoogleRemoteEntity>

    suspend fun fetchUserTwitch(
        userInfoUrl: String,
        accessToken: String,
        clientId: String
    ): Flow<UserTwitchRemoteEntity>
}

