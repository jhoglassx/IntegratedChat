package com.js.integratedchat.ui.chat.model

import com.js.integratedchat.domain.entity.UserEntity

sealed class ChatAction {
    data class LoadMessages(
        val userYouTube: UserEntity?,
        val userTwitch: UserEntity?
    ): ChatAction()
}