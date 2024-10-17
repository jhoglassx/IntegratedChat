package com.js.integratedchat.provider

import Constants.TWITCH_BADGES_CACHE
import Constants.TWITCH_TOKEN
import com.js.integratedchat.BuildConfig
import com.js.integratedchat.data.Keys
import com.js.integratedchat.data.entity.BadgeResponse
import com.js.integratedchat.data.entity.BadgesResponse
import com.js.integratedchat.service.ApiService
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
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
                    TWITCH_BADGES_CACHE.clear()
                    TWITCH_BADGES_CACHE.putAll(newBadgeMap)
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
                "Client-Id" to Keys.twitchClientId
            ),
            body = mapOf(

            ),
            queryParams = mapOf(
                //"broadcaster_id" to channel
            )
        )

        if (response.status == HttpStatusCode.OK) {
            val badgesResponse = response.body<BadgesResponse>()

            val badgeMap = badgesResponse.data.flatMap { badgeData ->
                badgeData.versions.map { badgeVersion ->
                    "$badgeData.set_id/${badgeVersion.id}" to badgeVersion
                }
            }.toMap().toMutableMap()

            return badgeMap

        } else {
            throw Exception("Failed to fetch token: ${response.status}, $response")
        }
    }
}