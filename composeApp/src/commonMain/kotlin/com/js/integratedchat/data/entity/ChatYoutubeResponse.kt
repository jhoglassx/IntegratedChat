package com.js.integratedchat.data.entity

import Constants.EMOTE_REGEX
import Constants.GOOGLE_LIVE_CHAT_ID
import com.js.integratedchat.domain.entity.MessageTypeEnum
import com.js.integratedchat.ext.parseDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatYoutubeResponse(
    val items: List<ChatItem>?,
    val nextPageToken: String?
)

@Serializable
data class ChatItem(
    val id: String,
    @SerialName("snippet")
    val message: Message,
    val authorDetails: AuthorDetails,
    val superChatDetails: SuperChatDetails?,
    val isPinned: Boolean
)

@Serializable
data class Message(
    val publishedAt: String,
    val displayMessage: String
)

@Serializable
data class AuthorDetails(
    val displayName: String,
    val channelId: String
)

@Serializable
data class SuperChatDetails(
    val amountMicros: String,
    val currency: String,
    val userChannelId: String,
    val tier: Int,
    val moderatorStatus: String,
)

fun ChatYoutubeResponse.toRemote(
    googleUser: UserRemoteEntity,
    badges: List<BadgeResponse>,
) = items?.map {
    it.toRemote(googleUser, badges)
} ?: emptyList()

fun ChatItem.toRemote(
    googleUser: UserRemoteEntity,
    badges: List<BadgeResponse>,
) = ChatMessageEntityRemote(
    id = id,
    userId = authorDetails.channelId,
    userName = authorDetails.displayName,
    displayName = authorDetails.displayName,
    timestamp = message.publishedAt.parseDateTime(),
    message = message.displayMessage,
    badges = badges,
    emotes = message.parseEmotes(),
    source = SourceEnum.YOUTUBE,
    channelId = GOOGLE_LIVE_CHAT_ID,
    channelName = googleUser.displayName ?: "Unknown"
)

fun Message.parseEmotes(): List<EmoteRemoteEntity> {
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