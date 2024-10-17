package com.js.integratedchat.data.entity

import kotlinx.serialization.Serializable

@Serializable
data class Snippet(
    val liveChatId: String?
)

@Serializable
data class Item(
    val snippet: Snippet?
)

@Serializable
data class LiveChatIdResponse(
    val items: List<Item>?
)
