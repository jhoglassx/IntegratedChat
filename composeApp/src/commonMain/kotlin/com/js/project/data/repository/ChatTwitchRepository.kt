package com.js.project.data.repository

import com.js.project.domain.entity.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

interface ChatTwitchRepository {

    suspend fun getTwitchChat(channel: String): Flow<ChatMessageEntity>
}