package com.js.project.data.datasource

import com.js.project.data.entity.ChatMessageEntityRemote
import kotlinx.coroutines.flow.Flow

interface ChatTwitchDataSource {

    suspend fun getTwitchChat(channel: String): Flow<ChatMessageEntityRemote>
}