package com.js.project.data.repository

import com.js.project.data.datasource.ChatYoutubeDataSource
import com.js.project.data.entity.toDomain
import com.js.project.domain.entity.ChatMessageEntity
import com.js.project.domain.entity.UserEntity
import com.js.project.domain.entity.toRemote
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

