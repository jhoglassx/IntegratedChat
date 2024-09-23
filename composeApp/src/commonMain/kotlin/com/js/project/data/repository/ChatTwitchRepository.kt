package com.js.project.data.repository

import com.js.project.domain.entity.ChatMessageEntity
import com.js.project.domain.entity.UserEntity
import kotlinx.coroutines.flow.Flow

interface ChatTwitchRepository {

    suspend fun getTwitchChat(userEntity: UserEntity): Flow<ChatMessageEntity>
}