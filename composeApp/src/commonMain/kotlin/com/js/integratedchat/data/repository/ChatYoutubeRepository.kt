package com.js.integratedchat.data.repository

import com.js.integratedchat.domain.entity.ChatMessageEntity
import com.js.integratedchat.domain.entity.UserEntity
import kotlinx.coroutines.flow.Flow

interface ChatYoutubeRepository {

    suspend fun getYouTubeChat(googleUser: UserEntity): Flow<ChatMessageEntity>

    suspend fun isLiveStreamActive(): Flow<Boolean>
}

