package com.js.integratedchat.data.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BadgesResponse(
    val data: List<BadgeData>
)

@Serializable
data class BadgeData(
    val set_id: String,
    val versions: List<BadgeResponse>
)

@Serializable
data class BadgeResponse(
    val id: String,
    @SerialName("image_url_1x")
    val imageUrl1x: String,
    @SerialName("image_url_2x")
    val imageUrl2x: String,
    @SerialName("image_url_4x")
    val imageUrl4x: String
)