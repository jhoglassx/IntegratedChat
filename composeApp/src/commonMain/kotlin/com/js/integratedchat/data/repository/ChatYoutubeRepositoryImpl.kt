package com.js.integratedchat.data.repository

import com.js.integratedchat.data.datasource.ChatYoutubeDataSource
import com.js.integratedchat.data.entity.toDomain
import com.js.integratedchat.domain.entity.ChatMessageEntity
import com.js.integratedchat.domain.entity.UserEntity
import com.js.integratedchat.domain.entity.toRemote
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChatYoutubeRepositoryImpl(
    private val chatYoutubeDataSource: ChatYoutubeDataSource
): ChatYoutubeRepository {

    override suspend fun getYouTubeChat(
        googleUser: UserEntity
    ): Flow<ChatMessageEntity> = chatYoutubeDataSource.getYouTubeChat(
        googleUser.toRemote()
    ).map {
        it.toDomain()
    }

    override suspend fun isLiveStreamActive(
    ): Flow<Boolean> = chatYoutubeDataSource.isLiveStreamActive()
}

