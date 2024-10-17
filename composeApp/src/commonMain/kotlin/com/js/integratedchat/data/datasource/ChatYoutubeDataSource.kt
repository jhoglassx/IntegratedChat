package com.js.integratedchat.data.datasource

import com.js.integratedchat.data.entity.ChatMessageEntityRemote
import com.js.integratedchat.data.entity.UserResponseRemoteEntity
import kotlinx.coroutines.flow.Flow

interface ChatYoutubeDataSource {

    suspend fun getYouTubeChat(
        googleUser: UserResponseRemoteEntity
    ): Flow<ChatMessageEntityRemote>

    suspend fun isLiveStreamActive(): Flow<Boolean>
}

