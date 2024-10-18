package com.js.integratedchat.data.entity

import kotlinx.serialization.Serializable

@Serializable
data class LiveBroadcastsResponse(
    val kind: String,
    val etag: String,
    val items: List<LiveBroadcastItem>
)

@Serializable
data class LiveBroadcastItem(
    val id: String,
    val status: LiveBroadcastStatus
)

@Serializable
data class LiveBroadcastStatus(
    val lifeCycleStatus: String,
    val privacyStatus: String,
    val recordingStatus: String
)