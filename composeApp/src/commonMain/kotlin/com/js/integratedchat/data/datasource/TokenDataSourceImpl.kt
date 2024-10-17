package com.js.integratedchat.data.datasource

import com.js.integratedchat.data.entity.TokenResponseRemoteEntity
import com.js.integratedchat.provider.DispatcherProvider
import com.js.integratedchat.service.ApiService
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class TokenDataSourceImpl(
    private val dispatcherProvider : DispatcherProvider,
    private val apiService: ApiService
): TokenDataSource {

    override suspend fun fetchToken(
        tokenUrl: String,
        clientId: String,
        clientSecret: String?,
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
            val responseBody = response.bodyAsText()
            val json = Json.parseToJsonElement(responseBody).jsonObject

            emit(
                TokenResponseRemoteEntity(
                    accessToken = json["access_token"]?.jsonPrimitive?.content ?: "",
                    refreshToken = json["refresh_token"]?.jsonPrimitive?.content,
                    expiresIn = json["expires_in"]?.jsonPrimitive?.int ?: 0,
                    tokenType = json["token_type"]?.jsonPrimitive?.content ?: ""
                )
            )
        } else {
            val errorResponse = response.body<String>()
            throw Exception("Failed to fetch token: ${response.status}, $errorResponse")
        }
    }.flowOn(dispatcherProvider.IO)
}
