package com.js.integratedchat.ui.app

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import com.js.integratedchat.ui.auth.model.AuthState


@Preview
@Composable
fun AppDesktopPreview(){
    App(
        onSignInTwitch = {},
        onSignInGoogle = {},
        authState = AuthState()
    )
}