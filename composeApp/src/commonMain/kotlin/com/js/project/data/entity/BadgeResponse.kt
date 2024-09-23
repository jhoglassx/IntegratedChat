package com.js.project.data.entity

import kotlinx.serialization.Serializable


@Serializable
data class BadgeResponse(
    val id: String,
    val image_url_1x: String,
    val image_url_2x: String,
    val image_url_4x: String
)
