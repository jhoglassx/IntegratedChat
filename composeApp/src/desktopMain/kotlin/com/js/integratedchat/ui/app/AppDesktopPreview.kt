package com.js.integratedchat.ui.app

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.js.integratedchat.data.datasource.SourceEnum
import com.js.integratedchat.domain.entity.ChatMessageEntity
import com.js.integratedchat.ui.auth.model.AuthState
import com.js.integratedchat.ui.chat.model.ChatState


@Preview
@Composable
fun AppDesktopPreview(){
    App(
        onSignInTwitch = {},
        onSignInGoogle = {},
        authState = AuthState()
    )
}