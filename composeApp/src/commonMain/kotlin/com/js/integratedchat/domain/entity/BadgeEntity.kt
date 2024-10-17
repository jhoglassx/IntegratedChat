package com.js.integratedchat.domain.entity

import kotlinx.serialization.Serializable


@Serializable
data class BadgeEntity(
    val badgeType: String?,
    val url: String
)
