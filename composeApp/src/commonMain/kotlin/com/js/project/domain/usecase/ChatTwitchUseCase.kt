package com.js.project.domain.usecase

import com.js.project.domain.entity.ChatMessageEntity
import com.js.project.domain.entity.UserEntity
import kotlinx.coroutines.flow.Flow

interface ChatTwitchUseCase {

    suspend fun getTwitchChat(
        userTwitch: UserEntity
    ): Flow<ChatMessageEntity>
}