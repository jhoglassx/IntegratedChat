package com.js.project.data.datasource

import Constants.GOOGLE_TOKEN
import com.js.project.service.ApiService
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class ChatYouTubeLiveIdDataSourceImpl(
    private val apiService: ApiService
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
            val responseBody = response.body<String>()
            val json = Json.parseToJsonElement(responseBody).jsonObject
            val items = json["items"]?.jsonArray ?: emptyList()

            var liveChatId = ""

            for (item in items) {
                val snippet = item.jsonObject["snippet"]?.jsonObject ?: continue
                liveChatId = snippet["liveChatId"]?.jsonPrimitive?.contentOrNull ?: ""
            }

            emit(liveChatId)
        } else {
            throw Exception("Failed to fetch live chat ID: ${response.status}")
        }
    }.flowOn(Dispatchers.IO)
}