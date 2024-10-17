package com.js.integratedchat.domain.usecase

import com.js.integratedchat.domain.entity.ChatMessageEntity
import com.js.integratedchat.domain.entity.UserEntity
import kotlinx.coroutines.flow.Flow

interface ChatYoutubeUseCase {

    suspend fun getYouTubeChat(googleUser: UserEntity): Flow<ChatMessageEntity>
}

