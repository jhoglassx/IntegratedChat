package com.js.project.service

import kotlinx.coroutines.flow.Flow

interface TwitchChatService{
    suspend fun getTwitchChatMessages(
        channel: String,
        twitchToken: String,
        twitchUsername: String
    ): Flow<String>
}