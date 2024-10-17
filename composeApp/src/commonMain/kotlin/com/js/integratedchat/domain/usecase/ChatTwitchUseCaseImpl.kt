package com.js.integratedchat.domain.usecase

import com.js.integratedchat.data.repository.ChatTwitchRepository
import com.js.integratedchat.domain.entity.ChatMessageEntity
import com.js.integratedchat.domain.entity.UserEntity
import kotlinx.coroutines.flow.Flow

class ChatTwitchUseCaseImpl(
    private val chatTwitchRepository: ChatTwitchRepository
) : ChatTwitchUseCase {
    override suspend fun getTwitchChat(
        userTwitch: UserEntity
    ): Flow<ChatMessageEntity> = chatTwitchRepository.getTwitchChat(userTwitch)
}