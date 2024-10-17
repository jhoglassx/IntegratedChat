package com.js.integratedchat.provider

import Constants.TWITCH_EMOTE_CACHE
import Constants.TWITCH_TOKEN
import com.js.integratedchat.BuildConfig
import com.js.integratedchat.data.entity.BadgeResponse
import com.js.integratedchat.service.ApiService
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.util.concurrent.TimeUnit

class BadgeCacheImpl(
    private val apiService: ApiService,
    private val dispatcherProvider: DispatcherProvider
): BadgeCache {

    override fun start(){
        CoroutineScope(dispatcherProvider.IO).launch {
            try {
                while (isActive) {
                    val newBadgeMap = fetchBadges()
                    TWITCH_EMOTE_CACHE.clear()
                    TWITCH_EMOTE_CACHE.putAll(newBadgeMap)
                    println("Badge cache updated")
                    delay(TimeUnit.HOURS.toMillis(1))
                }
            } catch (e: Exception) {
                println("Failed to update badge cache: ${e.message}")
            }
        }
    }

    private suspend fun fetchBadges(): MutableMap<String, BadgeResponse> {
        val response: HttpResponse = apiService.request(
            url = "https://api.twitch.tv/helix/chat/badges/global",
            method = HttpMethod.Get,
            headers = mapOf(
                "Content-Type" to "application/json",
                "Authorization" to "Bearer $TWITCH_TOKEN",
                "Client-Id" to BuildConfig.TWITCH_CLIENT_ID
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