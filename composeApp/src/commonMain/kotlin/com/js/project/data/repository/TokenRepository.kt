package com.js.project.data.repository

import com.js.project.domain.entity.TokenEntity
import kotlinx.coroutines.flow.Flow

interface TokenRepository {

    suspend fun fetchToken(
        tokenUrl: String,
        clientId: String,
        clientSecret: String,
        authorizationCode: String,
        redirectUri: String
    ): Flow<TokenEntity>
}
