package com.js.integratedchat.data.entity

import kotlinx.serialization.Serializable

@Serializable
data class LiveChatSnippet(
    val liveChatId: String?
)

@Serializable
data class LiveChatItem(
    val snippet: LiveChatSnippet?
)

@Serializable
data class LiveChatIdResponse(
    val items: List<LiveChatItem>?
)
