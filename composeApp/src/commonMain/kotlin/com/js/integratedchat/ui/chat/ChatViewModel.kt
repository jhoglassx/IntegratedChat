package com.js.integratedchat.ui.chat

import com.js.integratedchat.domain.entity.ChatMessageEntity
import com.js.integratedchat.domain.entity.UserEntity
import com.js.integratedchat.domain.usecase.ChatTwitchUseCase
import com.js.integratedchat.domain.usecase.ChatYoutubeUseCase
import com.js.integratedchat.provider.DispatcherProvider
import com.js.integratedchat.ui.base.BaseViewModel
import com.js.integratedchat.ui.chat.model.ChatAction
import com.js.integratedchat.ui.chat.model.ChatState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.launch

class ChatViewModel(
    private val chatTwitchUseCase: ChatTwitchUseCase,
    private val chatYoutubeUseCase: ChatYoutubeUseCase,
    private val dispatcherProvider: DispatcherProvider
): BaseViewModel(
    dispatcherProvider = dispatcherProvider
) {

    private val _uiState = MutableStateFlow(ChatState())
    val uiState = _uiState.asStateFlow()

    fun onAction(action: ChatAction) {
        when (action) {
            is ChatAction.LoadMessages -> getLiveChat(action.userTwitch, action.userYouTube)
        }
    }

    private fun getLiveChat(
        userTwitch: UserEntity?,
        userYouTube: UserEntity?
    ) {
        viewModelScope.launch(dispatcherProvider.MAIN) {
            val twitchMessages = getTwitchMessages(userTwitch)
            val youtubeMessages = getYoutubeMessages(userYouTube)

            val combinedFlow: Flow<ChatMessageEntity> = merge(twitchMessages, youtubeMessages)

            combinedFlow.collect { message ->
                val updatedList = _uiState.value.chatMessages.toMutableList()
                if(updatedList.contains(message).not()){
                    updatedList.add(message)
                    updatedList.sortBy { it.timestamp }
                    _uiState.value = _uiState.value.copy(chatMessages = updatedList)
                }

            }
        }
    }

    private suspend fun getYoutubeMessages(
        userYouTube: UserEntity?
    ): Flow<ChatMessageEntity> {
        return userYouTube?.let {
            chatYoutubeUseCase.getYouTubeChat(it)
        } ?: flowOf()
    }

    private suspend fun getTwitchMessages(
        userTwitch: UserEntity?
    ): Flow<ChatMessageEntity> {
        return userTwitch?.let {
            chatTwitchUseCase.getTwitchChat(it)
        } ?: flowOf()
    }
}