package com.js.project.ui.chat.model

import com.js.project.domain.entity.UserEntity

sealed class ChatAction {
    data class LoadMessages(
        val userYouTube: UserEntity?,
        val userTwitch: UserEntity?
    ): ChatAction()
}