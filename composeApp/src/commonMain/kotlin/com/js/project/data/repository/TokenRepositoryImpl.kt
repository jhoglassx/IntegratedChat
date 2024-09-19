package com.js.project.data.repository

import com.js.project.data.datasource.TokenDataSource
import com.js.project.data.entity.toRemote
import com.js.project.domain.entity.TokenEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last

class TokenRepositoryImpl(
    private val tokenDataSource: TokenDataSource
): TokenRepository {

    override suspend fun fetchToken(
        tokenUrl: String,
        clientId: String,
        clientSecret: String,
        authorizationCode: String,
        redirectUri: String
    ): Flow<TokenEntity> = flow {

        val result = tokenDataSource.fetchToken(
            tokenUrl = tokenUrl,
            clientId = clientId,
            clientSecret = clientSecret,
            authorizationCode = authorizationCode,
            redirectUri = redirectUri
        )
        emit(result.last().toRemote())
    }
}
