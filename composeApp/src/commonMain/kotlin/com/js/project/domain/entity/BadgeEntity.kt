package com.js.project.domain.entity

import kotlinx.serialization.Serializable


@Serializable
data class BadgeEntity(
    val badgeType: String?,
    val url: String
)
