package com.js.integratedchat.data.repository

import com.js.integratedchat.data.datasource.ChatTwitchDataSource
import com.js.integratedchat.data.entity.toDomain
import com.js.integratedchat.domain.entity.ChatMessageEntity
import com.js.integratedchat.domain.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChatTwitchRepositoryImpl(
    private val chatTwitchDataSource: ChatTwitchDataSource
) : ChatTwitchRepository {

    override suspend fun getTwitchChat(
        userEntity: UserEntity
    ): Flow<ChatMessageEntity> = chatTwitchDataSource.getTwitchChat(userEntity)
        .map {
            it.toDomain()
        }
}