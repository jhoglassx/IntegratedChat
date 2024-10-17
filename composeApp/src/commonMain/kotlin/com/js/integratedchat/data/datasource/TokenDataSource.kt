package com.js.integratedchat.data.datasource

import com.js.integratedchat.data.entity.TokenResponseRemoteEntity
import kotlinx.coroutines.flow.Flow

interface TokenDataSource {

    suspend fun fetchToken(
        tokenUrl: String,
        clientId: String,
        clientSecret: String,
        authorizationCode: String,
        redirectUri: String
    ): Flow<TokenResponseRemoteEntity>
}
