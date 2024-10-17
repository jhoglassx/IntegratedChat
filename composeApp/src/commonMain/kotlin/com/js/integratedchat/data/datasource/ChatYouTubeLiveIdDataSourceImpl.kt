package com.js.integratedchat.data.datasource

import Constants.GOOGLE_TOKEN
import co.touchlab.kermit.Logger
import com.js.integratedchat.ext.error
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
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class ChatYouTubeLiveIdDataSourceImpl(
    private val apiService: ApiService,
    private val dispatcherProvider : DispatcherProvider,
): ChatYouTubeLiveIdDataSource {

    override suspend fun getYouTubeLiveChatId(
        channelId: String
    ): Flow<String> = flow {

        val url ="https://www.googleapis.com/youtube/v3/liveBroadcasts"

        val response: HttpResponse = apiService.request(
            url = url,
            method = HttpMethod.Get,
            headers = mapOf(
                "Authorization" to "Bearer $GOOGLE_TOKEN",
            ),
            queryParams = mapOf(
                "channelId" to channelId,
                "broadcastType" to "all",
                "broadcastStatus" to "active",
                "part" to "snippet"
            )
        )

        if (response.status == HttpStatusCode.OK) {
            val responseBody = response.bodyAsText()
            val json = Json.parseToJsonElement(responseBody).jsonObject
            val items = json["items"]?.jsonArray ?: emptyList()

            var liveChatId = ""

            for (item in items) {
                val snippet = item.jsonObject["snippet"]?.jsonObject ?: continue
                liveChatId = snippet["liveChatId"]?.jsonPrimitive?.contentOrNull ?: ""
            }

            emit(liveChatId)
        } else {
            val error = Exception("Failed to fetch live chat ID: $response")
            Logger.error(
                tag = "ChatYoutubeDataSourceImpl",
                throwable = error,
                message = "isLiveStreamActive: $error"
            )
            throw error
        }
    }.flowOn(dispatcherProvider.IO)
}