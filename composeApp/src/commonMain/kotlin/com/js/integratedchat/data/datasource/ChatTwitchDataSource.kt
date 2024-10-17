package com.js.integratedchat.data.datasource

import com.js.integratedchat.data.entity.ChatMessageEntityRemote
import com.js.integratedchat.domain.entity.UserEntity
import kotlinx.coroutines.flow.Flow

interface ChatTwitchDataSource {

    suspend fun getTwitchChat(userEntity: UserEntity): Flow<ChatMessageEntityRemote>
}