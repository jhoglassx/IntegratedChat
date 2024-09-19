package com.js.project.domain.usecase

import com.js.project.data.repository.ChatTwitchRepository
import com.js.project.domain.entity.ChatMessageEntity
import com.js.project.domain.entity.UserEntity
import kotlinx.coroutines.flow.Flow

class ChatTwitchUseCaseImpl(
    private val chatTwitchRepository: ChatTwitchRepository
) : ChatTwitchUseCase {
    override suspend fun getTwitchChat(
        userTwitch: UserEntity
    ): Flow<ChatMessageEntity> = chatTwitchRepository.getTwitchChat(userTwitch.name)
}