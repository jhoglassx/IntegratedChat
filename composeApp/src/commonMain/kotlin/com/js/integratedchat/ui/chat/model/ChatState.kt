package com.js.integratedchat.ui.chat.model

import com.js.integratedchat.domain.entity.ChatMessageEntity
import com.js.integratedchat.ui.base.BaseState

data class ChatState (
    val chatMessages: MutableList<ChatMessageEntity> = mutableListOf()
): BaseState()