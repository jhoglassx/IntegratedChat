package com.js.project.domain.usecase

import com.js.project.domain.entity.ChatMessageEntity
import com.js.project.domain.entity.UserEntity
import kotlinx.coroutines.flow.Flow

interface ChatYoutubeUseCase {

    suspend fun getYouTubeChat(googleUser: UserEntity): Flow<ChatMessageEntity>
}

