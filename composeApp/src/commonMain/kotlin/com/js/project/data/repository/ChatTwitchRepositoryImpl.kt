package com.js.project.data.repository

import com.js.project.data.datasource.ChatTwitchDataSource
import com.js.project.data.entity.toDomain
import com.js.project.domain.entity.ChatMessageEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChatTwitchRepositoryImpl(
    private val chatTwitchDataSource: ChatTwitchDataSource
) : ChatTwitchRepository {

    override suspend fun getTwitchChat(
        channel: String
    ): Flow<ChatMessageEntity> = chatTwitchDataSource.getTwitchChat(channel)
        .map {
            it.toDomain()
        }
}