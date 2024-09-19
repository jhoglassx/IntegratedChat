package com.js.project.data.repository

import com.js.project.domain.entity.ChatMessageEntity
import com.js.project.domain.entity.UserEntity
import kotlinx.coroutines.flow.Flow

interface ChatYoutubeRepository {

    suspend fun getYouTubeChat(googleUser: UserEntity): Flow<ChatMessageEntity>

    suspend fun isLiveStreamActive(): Flow<Boolean>
}

