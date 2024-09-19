package com.js.project.data.datasource

import Constants.TWITCH_TOKEN
import com.js.project.data.entity.ChatMessageEntityRemote
import com.js.project.data.entity.EmoteRemoteEntity
import com.js.project.service.TwitchChatService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Instant

class ChatTwitchDataSourceImpl(
    private val twitchChatService: TwitchChatService
): ChatTwitchDataSource {

    override suspend fun getTwitchChat(
        channel: String
    ): Flow<ChatMessageEntityRemote> = flow {
        twitchChatService.getTwitchChatMessages(
            channel = channel,
            twitchUsername = channel,
            twitchToken = TWITCH_TOKEN
        ).collect {
            val parse = parseTwitchMessage(it, channel)
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
            if (keyValue.size == 2) keyValue[0] to keyValue[1] else null
        }?.toMap() ?: emptyMap()

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
                            EmoteRemoteEntity(start, end)
                        } else {
                            null
                        }
                    } else {
                        null
                    }
                }
                emoteId to ranges
            } else {
                null
            }
        }?.toMap() ?: emptyMap()

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
}