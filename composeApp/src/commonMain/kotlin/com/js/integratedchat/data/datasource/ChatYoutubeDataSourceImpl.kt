package com.js.integratedchat.data.datasource

import Constants.EMOTE_REGEX
import Constants.GOOGLE_LIVE_CHAT_ID
import Constants.GOOGLE_TOKEN
import co.touchlab.kermit.Logger
import com.js.integratedchat.data.entity.BadgeResponse
import com.js.integratedchat.data.entity.ChatMessageEntityRemote
import com.js.integratedchat.data.entity.EmotePositionRemoteEntity
import com.js.integratedchat.data.entity.EmoteRemoteEntity
import com.js.integratedchat.data.entity.UserResponseRemoteEntity
import com.js.integratedchat.ext.error
import com.js.integratedchat.ext.parseDateTime
import com.js.integratedchat.provider.DispatcherProvider
import com.js.integratedchat.service.ApiService
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
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class ChatYoutubeDataSourceImpl(
    private val dispatcherProvider : DispatcherProvider,
    private val apiService: ApiService
): ChatYoutubeDataSource {

    @OptIn(InternalAPI::class)
    override suspend fun getYouTubeChat(
        googleUser: UserResponseRemoteEntity
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

                    if (response.status == HttpStatusCode.OK) {val responseBody = response.bodyAsText()
                        val json = Json.parseToJsonElement(responseBody).jsonObject
                        val items = json["items"]?.jsonArray ?: emptyList()
                        nextPageToken = json["nextPageToken"]?.jsonPrimitive?.contentOrNull ?: ""

                        for (item in items) {
                            val snippet = item.jsonObject["snippet"]?.jsonObject ?: continue
                            val authorDetails =
                                item.jsonObject["authorDetails"]?.jsonObject ?: continue
                            val displayName =
                                authorDetails["displayName"]?.jsonPrimitive?.contentOrNull
                                    ?: continue
                            val messageId =
                                item.jsonObject["id"]?.jsonPrimitive?.contentOrNull ?: continue
                            val publishedAt =
                                snippet["publishedAt"]?.jsonPrimitive?.contentOrNull ?: continue
                            val displayMessage =
                                snippet["displayMessage"]?.jsonPrimitive?.contentOrNull ?: continue
                            val timestamp = publishedAt.parseDateTime() ?: continue
                            val userId = authorDetails["channelId"]?.jsonPrimitive?.contentOrNull
                            val userName = authorDetails["channelId"]?.jsonPrimitive?.contentOrNull
                            val channelId = GOOGLE_LIVE_CHAT_ID.toString()
                            val channelName = googleUser.displayName ?: "Unknown"

                            // Determine badges
                            val badges = listOf<BadgeResponse>()
                            val emotes = parseEmotes(displayMessage)

                            emit(
                                ChatMessageEntityRemote(
                                    id = messageId,
                                    userId = userId,
                                    userName = userName,
                                    displayName = displayName,
                                    timestamp = timestamp,
                                    message = displayMessage,
                                    badges = badges,
                                    emotes = emotes,
                                    source = SourceEnum.YOUTUBE,
                                    channelId = channelId,
                                    channelName = channelName
                                )
                            )
                        }
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
                    val responseBody = response.bodyAsText()
                    val json = Json.parseToJsonElement(responseBody).jsonObject
                    val items = json["items"]?.jsonArray ?: emptyList()

                    emit(items.isNotEmpty())
                    if(items.isNotEmpty()){
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

    private fun parseEmotes(displayMessage: String): List<EmoteRemoteEntity> {
        val emotes = mutableListOf<EmoteRemoteEntity>()
        val matches = EMOTE_REGEX.findAll(displayMessage)

        matches.forEach { match ->
            val id = match.value
            val start = match.range.first
            val end = match.range.last + 1

            emotes.add(
                EmoteRemoteEntity(
                    emoteId = id,
                    imgUrl = EmoteUrlEnum.fromLabel(id).url ,
                    positions = listOf(EmotePositionRemoteEntity(start, end))
                )
            )
        }

        return emotes
    }
}

