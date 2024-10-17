package com.js.integratedchat.data.datasource

import com.js.integratedchat.data.entity.UserResponseRemoteEntity
import com.js.integratedchat.provider.DispatcherProvider
import com.js.integratedchat.service.ApiService
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class UserDataSourceImpl(
    private val apiService: ApiService,
    private val dispatcherProvider : DispatcherProvider,
) : UserDataSource {

    override suspend fun fetchUserGoogle(
        userInfoUrl: String,
        accessToken: String,
        clientId: String
    ): Flow<UserResponseRemoteEntity> = flow {

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
            val responseBody = response.bodyAsText()
            val json = Json.parseToJsonElement(responseBody).jsonObject

            val user = UserResponseRemoteEntity(
                id = json["id"]?.jsonPrimitive?.content ?: "",
                email = json["email"]?.jsonPrimitive?.content ?: "",
                name = json["given_name"]?.jsonPrimitive?.content ?: "",
                displayName = json["name"]?.jsonPrimitive?.content ?: "",
                imageUrl = json["picture"]?.jsonPrimitive?.content
            )
            emit(user)
        }
    }.flowOn(dispatcherProvider.IO)

    override suspend fun fetchUserTwitch(
        userInfoUrl: String,
        accessToken: String,
        clientId: String
    ): Flow<UserResponseRemoteEntity> = flow{

        val response: HttpResponse = apiService.request(
            url = userInfoUrl,
            method = HttpMethod.Get,
            headers = mapOf(
                "Authorization" to "Bearer $accessToken",
                "Client-Id" to clientId
            )
        )

        if (response.status == HttpStatusCode.OK) {
            val responseBody = response.bodyAsText()
            val json = Json.parseToJsonElement(responseBody).jsonObject

            val userData = json["data"]?.jsonArray?.firstOrNull()?.jsonObject

            emit(
                UserResponseRemoteEntity(
                    id = userData?.get("id")?.jsonPrimitive?.content ?: "",
                    email = userData?.get("email")?.jsonPrimitive?.content ?: "",
                    name = userData?.get("login")?.jsonPrimitive?.content ?: "",
                    displayName = userData?.get("display_name")?.jsonPrimitive?.content ?: "",
                    imageUrl = userData?.get("profile_image_url")?.jsonPrimitive?.content
                )
            )
        }
    }.flowOn(dispatcherProvider.IO)
}
