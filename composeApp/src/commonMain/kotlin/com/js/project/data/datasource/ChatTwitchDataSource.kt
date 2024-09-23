package com.js.project.data.datasource

import com.js.project.data.entity.ChatMessageEntityRemote
import com.js.project.domain.entity.UserEntity
import kotlinx.coroutines.flow.Flow

interface ChatTwitchDataSource {

    suspend fun getTwitchChat(userEntity: UserEntity): Flow<ChatMessageEntityRemote>
}