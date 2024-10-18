package com.js.integratedchat.data.datasource

import Constants.GOOGLE_LIVE_CHAT_ID
import Constants.GOOGLE_TOKEN
import co.touchlab.kermit.Logger
import com.js.integratedchat.data.entity.ChatMessageEntityRemote
import com.js.integratedchat.data.entity.ChatYoutubeResponse
import com.js.integratedchat.data.entity.LiveBroadcastsResponse
import com.js.integratedchat.data.entity.UserRemoteEntity
import com.js.integratedchat.data.entity.toRemote
import com.js.integratedchat.ext.error
import com.js.integratedchat.provider.DispatcherProvider
import com.js.integratedchat.service.ApiService
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.util.InternalAPI
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject

class ChatYoutubeDataSourceImpl(
    private val dispatcherProvider : DispatcherProvider,
    private val apiService: ApiService
): ChatYoutubeDataSource {

    @OptIn(InternalAPI::class)
    override suspend fun getYouTubeChat(
        googleUser: UserRemoteEntity
    ): Flow<ChatMessageEntityRemote> = flow {

        var nextPageToken = ""
            try {
                while (true) {
                    val url = "https://www.googleapis.com/youtube/v3/liveChat/messages"

                    val response: HttpResponse = apiService.request(
                        url = url,
                        method = HttpMethod.Get,
                        headers = mapOf(
                            "Authorization" to "Bearer $GOOGLE_TOKEN",
                        ),
                        queryParams = mapOf(
                            "liveChatId" to GOOGLE_LIVE_CHAT_ID,
                            "part" to "snippet,authorDetails",
                            "pageToken" to nextPageToken
                        )
                    )

                    if (response.status == HttpStatusCode.OK) {
                        val chatResponse = response.body<ChatYoutubeResponse>()
                        val chatMessages = chatResponse.toRemote(
                            googleUser = googleUser,
                            badges = emptyList()
                        )

                        chatMessages.forEach {
                            emit(it)
                        }
                        break
                    } else {
                        Logger.error(
                            tag = "ChatYoutubeDataSourceImpl",
                            throwable = Throwable(response.toString()),
                            message ="getYouTubeChat ${response.status} - ${response.content}"
                        )
                    }
                    delay(5000)
                }
            } catch (e: Exception) {
                Logger.error(
                    tag = "ChatYoutubeDataSourceImpl",
                    throwable = e,
                    message = "getYouTubeChat: ${e.message}"
                )
            }
    }.flowOn(dispatcherProvider.IO)

    @OptIn(InternalAPI::class)
    override suspend fun isLiveStreamActive(): Flow<Boolean> = flow {
        try {
            while (true) {
                val url = "https://www.googleapis.com/youtube/v3/liveBroadcasts"

                val response: HttpResponse = apiService.request(
                    url = url,
                    method = HttpMethod.Get,
                    headers = mapOf(
                        "Authorization" to "Bearer $GOOGLE_TOKEN",
                    ),
                    queryParams = mapOf(
                        "liveChatId" to GOOGLE_LIVE_CHAT_ID,
                        "part" to "status",
                        "broadcastStatus" to "active",
                        "broadcastType" to "all",
                    )
                )

                if (response.status == HttpStatusCode.OK) {
                    val responseBody = response.body<LiveBroadcastsResponse>()

                    val hasItems = responseBody.items.isNotEmpty()
                    emit(hasItems)
                    if(hasItems){
                        break
                    }
                } else {
                    Logger.error(
                        tag = "ChatYoutubeDataSourceImpl",
                        throwable = Throwable(response.toString()),
                        message = "isLiveStreamActive: ${response.status} - ${response.content}"
                    )
                    emit(false)
                }
                delay(5000)
            }
        } catch (e: Exception) {
            Logger.error(
                tag = "ChatYoutubeDataSourceImpl",
                throwable = e,
                message = "isLiveStreamActive: ${e.message}"
            )
            emit(false)
        }
    }.flowOn(dispatcherProvider.IO)
}

