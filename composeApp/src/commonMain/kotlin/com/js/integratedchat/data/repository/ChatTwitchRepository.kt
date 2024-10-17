package com.js.integratedchat.data.repository

import com.js.integratedchat.domain.entity.ChatMessageEntity
import com.js.integratedchat.domain.entity.UserEntity
import kotlinx.coroutines.flow.Flow

interface ChatTwitchRepository {

    suspend fun getTwitchChat(userEntity: UserEntity): Flow<ChatMessageEntity>
}