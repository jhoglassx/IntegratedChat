package com.js.integratedchat.domain.usecase

import com.js.integratedchat.domain.entity.ChatMessageEntity
import com.js.integratedchat.domain.entity.UserEntity
import kotlinx.coroutines.flow.Flow

interface ChatTwitchUseCase {

    suspend fun getTwitchChat(
        userTwitch: UserEntity
    ): Flow<ChatMessageEntity>
}