package com.js.project.ui.chat.model

import com.js.project.domain.entity.ChatMessageEntity
import com.js.project.ui.base.BaseState

data class ChatState (
    val chatMessages: MutableList<ChatMessageEntity> = mutableListOf()
): BaseState()