package com.js.project.data.datasource

import com.js.project.data.entity.ChatMessageEntityRemote
import com.js.project.data.entity.UserResponseRemoteEntity
import kotlinx.coroutines.flow.Flow

interface ChatYoutubeDataSource {

    suspend fun getYouTubeChat(
        googleUser: UserResponseRemoteEntity
    ): Flow<ChatMessageEntityRemote>

    suspend fun isLiveStreamActive(): Flow<Boolean>
}

