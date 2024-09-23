package com.js.project.data.datasource

import Constants.TWITCH_TOKEN
import com.js.project.data.entity.BadgeResponse
import com.js.project.data.entity.ChatMessageEntityRemote
import com.js.project.data.entity.EmotePositionRemoteEntity
import com.js.project.data.entity.EmoteRemoteEntity
import com.js.project.domain.entity.UserEntity
import com.js.project.provider.DispatcherProvider
import com.js.project.provider.KeysConfig
import com.js.project.service.ApiService
import com.js.project.service.TwitchChatService
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

class ChatTwitchDataSourceImpl(
    private val twitchChatService: TwitchChatService,
    private val apiService: ApiService,
    private val dispatcherProvider : DispatcherProvider,
): ChatTwitchDataSource {

    private val badgeCache = ConcurrentHashMap<String, BadgeResponse>()

    override suspend fun getTwitchChat(
        userEntity: UserEntity
    ): Flow<ChatMessageEntityRemote> = flow {

        startBadgeCacheUpdater(userEntity.id)

        twitchChatService.getTwitchChatMessages(
            channel = userEntity.name,
            twitchUsername = userEntity.name,
            twitchToken = TWITCH_TOKEN
        ).collect {
            val parse = parseTwitchMessage(it, userEntity.name)
            parse?.let {
                emit(parse)
            }
        }
    }

    private fun parseTwitchMessage(
        line: String,
        channel: String
    ): ChatMessageEntityRemote? {

        val tagsPart = line.substringBefore(" :")
        val messagePart = line.substringAfter("PRIVMSG #$channel :")

        if (tagsPart.isEmpty() || messagePart.isEmpty()) {
            return null
        }

        val tags = tagsPart.split(";").mapNotNull {
            val keyValue = it.split("=")
            if (keyValue.size == 2) keyValue[0] to keyValue[1] else null
        }.toMap()

        val badges = tags["badges"]?.split(",")?.mapNotNull {
            val keyValue = it.split("/")
            if (keyValue.size == 2) badgeCache[keyValue[0] + "/" + keyValue[1]] else null
        }

        val emotes = tags["emotes"]?.split("/")?.mapNotNull {
            val emoteParts = it.split(":")
            if (emoteParts.size == 2) {
                val emoteId = emoteParts[0]
                val ranges = emoteParts[1].split(",").mapNotNull { range ->
                    val rangeParts = range.split("-")
                    if (rangeParts.size == 2) {
                        val start = rangeParts[0].toIntOrNull()
                        val end = rangeParts[1].toIntOrNull()
                        if (start != null && end != null) {
                            EmotePositionRemoteEntity(start, end)
                        } else {
                            null
                        }
                    } else {
                        null
                    }
                }
                EmoteRemoteEntity(
                    emoteId = emoteId,
                    positions = ranges
                )
            } else {
                null
            }
        }

        return ChatMessageEntityRemote(
            id = tags["id"] ?: return null,
            userId = tags["user-id"],
            userName = tags["login"],
            displayName = tags["display-name"] ?: "Unknown",
            timestamp = tags["tmi-sent-ts"]?.toLongOrNull()?.let { Instant.fromEpochMilliseconds(it) },
            message = messagePart,
            badges = badges,
            emotes = emotes,
            source = "Twitch",
            channelId = tags["room-id"],
            channelName = channel
        )
    }

    private fun startBadgeCacheUpdater(
        channelId: String
    ){
        CoroutineScope(dispatcherProvider.IO).launch {
            try {
                while (isActive) {
                    val newBadgeMap = fetchBadges(channelId)
                    badgeCache.clear()
                    badgeCache.putAll(newBadgeMap)
                    println("Badge cache updated")
                    delay(TimeUnit.HOURS.toMillis(1))
                }
            } catch (e: Exception) {
                println("Failed to update badge cache: ${e.message}")
            }
        }
    }

    private suspend fun fetchBadges(
        channel: String
    ): MutableMap<String, BadgeResponse> {
        val response: HttpResponse = apiService.request(
            url = "https://api.twitch.tv/helix/chat/badges/global",
            method = HttpMethod.Get,
            headers = mapOf(
                "Content-Type" to "application/json",
                "Authorization" to "Bearer $TWITCH_TOKEN",
                "Client-Id" to KeysConfig.twitchClientId
            ),
            body = mapOf(

            ),
            queryParams = mapOf(
                //"broadcaster_id" to channel
            )
        )

        if (response.status == HttpStatusCode.OK) {
            val responseBody = response.bodyAsText()
            val json = Json.parseToJsonElement(responseBody).jsonObject
            val badgeDataArray = json["data"]?.jsonArray ?: throw Exception("No data found")
            val badgeMap = mutableMapOf<String, BadgeResponse>()
            for (badgeDataElement in badgeDataArray) {
                val badgeObject = badgeDataElement.jsonObject
                val setId = badgeObject["set_id"]?.jsonPrimitive?.content ?: continue
                val versionsArray = badgeObject["versions"]?.jsonArray ?: continue

                for (versionElement in versionsArray) {
                    val badgeVersionObject = versionElement.jsonObject
                    val id = badgeVersionObject["id"]?.jsonPrimitive?.content ?: continue
                    val imageUrl1x = badgeVersionObject["image_url_1x"]?.jsonPrimitive?.content ?: continue
                    val imageUrl2x = badgeVersionObject["image_url_2x"]?.jsonPrimitive?.content ?: continue
                    val imageUrl4x = badgeVersionObject["image_url_4x"]?.jsonPrimitive?.content ?: continue

                    println("Adding to badgeMap: $setId/$id = $id")

                    val badgeResponse = BadgeResponse(
                        id = id,
                        image_url_1x = imageUrl1x,
                        image_url_2x = imageUrl2x,
                        image_url_4x = imageUrl4x
                    )

                    badgeMap["$setId/$id"] = badgeResponse
                }
            }
            return badgeMap
        } else {
            val errorResponse = response.bodyAsText()
            throw Exception("Failed to fetch token: ${response.status}, $errorResponse")
        }
    }
}