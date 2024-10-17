package com.js.integratedchat.domain.usecase

import Constants.GOOGLE_LIVE_CHAT_ID
import com.js.integratedchat.data.repository.ChatYouTubeLiveIdRepository
import com.js.integratedchat.data.repository.ChatYoutubeRepository
import com.js.integratedchat.domain.entity.ChatMessageEntity
import com.js.integratedchat.domain.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last

class ChatYoutubeUseCaseImpl(
    private val chatYouTubeLiveIdRepository: ChatYouTubeLiveIdRepository,
    private val chatYoutubeRepository: ChatYoutubeRepository
): ChatYoutubeUseCase {

    override suspend fun getYouTubeChat(
        googleUser: UserEntity
    ): Flow<ChatMessageEntity> = flow {
        val isLiveStreamActive = isLiveStreamActive().last()
        if(isLiveStreamActive) {
            GOOGLE_LIVE_CHAT_ID =
                chatYouTubeLiveIdRepository.getYouTubeLiveChatId(googleUser.id).last()

            if (GOOGLE_LIVE_CHAT_ID.isEmpty().not()) {
                chatYoutubeRepository.getYouTubeChat(googleUser).collect {
                    emit(it)
                }
            }
        }
    }

    private suspend fun isLiveStreamActive(
    ): Flow<Boolean> = chatYoutubeRepository.isLiveStreamActive()
}

