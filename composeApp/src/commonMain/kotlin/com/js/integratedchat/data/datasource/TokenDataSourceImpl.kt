package com.js.integratedchat.data.datasource

import com.js.integratedchat.data.entity.TokenResponseRemoteEntity
import com.js.integratedchat.provider.DispatcherProvider
import com.js.integratedchat.service.ApiService
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class TokenDataSourceImpl(
    private val dispatcherProvider : DispatcherProvider,
    private val apiService: ApiService
): TokenDataSource {

    override suspend fun fetchToken(
        tokenUrl: String,
        clientId: String,
        clientSecret: String,
        authorizationCode: String,
        redirectUri: String
    ): Flow<TokenResponseRemoteEntity> = flow {
        val response: HttpResponse = apiService.request(
            url = tokenUrl,
            method = HttpMethod.Post,
            headers = mapOf("Content-Type" to "application/x-www-form-urlencoded"),
            body = mapOf(
                "client_id" to clientId,
                "client_secret" to clientSecret,
                "code" to authorizationCode,
                "grant_type" to "authorization_code",
                "redirect_uri" to redirectUri,
            )
        )

        if (response.status == HttpStatusCode.OK) {
            val token = response.body<TokenResponseRemoteEntity>()
            emit(token)
        } else {
            throw Exception("Failed to fetch token: ${response.status}, $response")
        }
    }.flowOn(dispatcherProvider.IO)
}
