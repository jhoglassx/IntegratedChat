package com.js.integratedchat.data.datasource

import com.js.integratedchat.data.entity.UserGoogleRemoteEntity
import com.js.integratedchat.data.entity.UserResponse
import com.js.integratedchat.data.entity.UserTwitchRemoteEntity
import com.js.integratedchat.provider.DispatcherProvider
import com.js.integratedchat.service.ApiService
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class UserDataSourceImpl(
    private val apiService: ApiService,
    private val dispatcherProvider : DispatcherProvider,
) : UserDataSource {

    override suspend fun fetchUserGoogle(
        userInfoUrl: String,
        accessToken: String,
        clientId: String
    ): Flow<UserGoogleRemoteEntity> = flow {

        val response: HttpResponse = apiService.request(
            url = userInfoUrl,
            method = HttpMethod.Get,
            headers = mapOf(
                "Authorization" to "Bearer $accessToken",
                "Client-Id" to clientId
            ),
            queryParams = mapOf(
                "alt" to "json"
            )
        )

        if (response.status == HttpStatusCode.OK) {
            val user = response.body<UserGoogleRemoteEntity>()
            emit(user)
        }
    }.flowOn(dispatcherProvider.IO)

    override suspend fun fetchUserTwitch(
        userInfoUrl: String,
        accessToken: String,
        clientId: String
    ): Flow<UserTwitchRemoteEntity> = flow{

        val response: HttpResponse = apiService.request(
            url = userInfoUrl,
            method = HttpMethod.Get,
            headers = mapOf(
                "Authorization" to "Bearer $accessToken",
                "Client-Id" to clientId
            )
        )

        if (response.status == HttpStatusCode.OK) {
            val user = response.body<UserResponse>()
            emit(user.data.first())
        }
    }.flowOn(dispatcherProvider.IO)
}
