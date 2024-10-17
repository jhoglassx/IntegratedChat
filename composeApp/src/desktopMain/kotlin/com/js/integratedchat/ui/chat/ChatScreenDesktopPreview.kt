package com.js.integratedchat.ui.chat

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.js.integratedchat.data.datasource.SourceEnum
import com.js.integratedchat.domain.entity.ChatMessageEntity
import com.js.integratedchat.ui.chat.model.ChatState


@Preview
@Composable
fun ChatMessageDesktopPreview(){
    val chatState = ChatState(
        chatMessages = mutableListOf(
            ChatMessageEntity(
                id = "chatMessage_1",
                userId = "userId_1",
                userName = "userName_1",
                displayName = "displayName_1",
                message = "message_1",
                source = SourceEnum.TWITCH,
                channelId = "channelId_1",
                channelName = "channelName_1"
            ),
            ChatMessageEntity(
                id = "chatMessage_2",
                userId = "userId_2",
                userName = "userName_2",
                displayName = "displayName_2",
                message = "message_2",
                source = SourceEnum.YOUTUBE,
                channelId = "channelId_2",
                channelName = "channelName_2"
            )
        )
    )

    ChatMessage(
        innerPadding = PaddingValues(12.dp),
        chatState = chatState
    )
}