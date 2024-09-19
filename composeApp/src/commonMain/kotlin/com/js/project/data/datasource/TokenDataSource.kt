package com.js.project.data.datasource

import com.js.project.data.entity.TokenResponseRemoteEntity
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
