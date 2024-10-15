package com.js.project.data.datasource

import androidx.compose.ui.graphics.Color
import integratedchat.composeapp.generated.resources.Res
import integratedchat.composeapp.generated.resources.ic_twitch
import integratedchat.composeapp.generated.resources.ic_youtube
import org.jetbrains.compose.resources.DrawableResource


enum class SourceEnum(
    val icon: DrawableResource,
    val description: String,
    val color: Color
) {
    YOUTUBE(
        icon = Res.drawable.ic_youtube,
        description = "YouTube",
        color = Color(0xFFFF0000)
    ),

    TWITCH(
        icon = Res.drawable.ic_twitch,
        description = "Twitch",
        color = Color(0xFF9146FF)
    )
}