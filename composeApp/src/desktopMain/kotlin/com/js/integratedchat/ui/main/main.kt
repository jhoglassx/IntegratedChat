package com.js.integratedchat.ui.main

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.js.integratedchat.di.initKoin
import com.js.integratedchat.service.ServerService
import com.js.integratedchat.provider.DispatcherProvider
import com.js.integratedchat.ui.app.App
import com.js.integratedchat.ui.auth.model.AuthAction
import com.js.integratedchat.ui.auth.model.AuthState
import com.js.integratedchat.ui.auth.AuthViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.awt.Desktop
import java.net.URI

class MainDesktop : KoinComponent {
    val authManager: ServerService by inject()
    val authViewModel: AuthViewModel by inject()
    val dispatcherProvider: DispatcherProvider by inject()
}

fun main() {
    initKoin()

    application {

        Window(
            onCloseRequest = ::exitApplication,
            title = "IntegratedChat"
        ) {
            val mainDesktop = MainDesktop()
            val authViewModel = mainDesktop.authViewModel
            val dispatcherProvider = mainDesktop.dispatcherProvider

            val uiState by authViewModel.uiState.collectAsState()
            if (uiState.authGoogleIntent == null) {
                authViewModel.onAction(AuthAction.GetGoogleSignInIntent)
            }
            if (uiState.authTwitchIntent == null) {
                authViewModel.onAction(AuthAction.GetTwitchSignInIntent)
            }

            App(
                onSignInGoogle = {
                    startGoogleSignInIntent(uiState,
                        authViewModel = authViewModel,
                        authManager = mainDesktop.authManager,
                        dispatcherProvider
                    )
                },
                onSignInTwitch = {
                    startTwitchSignInIntent(
                        uiState = uiState,
                        authViewModel = authViewModel,
                        authManager = mainDesktop.authManager,
                        dispatcherProvider = dispatcherProvider
                    )
                },
                authState = uiState
            )
        }
    }
}

fun startGoogleSignInIntent(
    uiState: AuthState,
    authViewModel: AuthViewModel,
    authManager: ServerService,
    dispatcherProvider: DispatcherProvider
) {
    CoroutineScope(dispatcherProvider.IO).launch {
        handleServerGoogle(
            authViewModel = authViewModel,
            authManager = authManager
        )
        Desktop.getDesktop().browse(URI(uiState.authGoogleIntent as String))
    }
}

fun startTwitchSignInIntent(
    uiState: AuthState,
    authViewModel: AuthViewModel,
    authManager: ServerService,
    dispatcherProvider: DispatcherProvider
) {
    CoroutineScope(dispatcherProvider.IO).launch {
        handleServerTwitch(
            authViewModel = authViewModel,
            authManager = authManager
        )
        Desktop.getDesktop().browse(URI(uiState.authTwitchIntent as String))
    }
}

private fun handleServerTwitch(
    authViewModel: AuthViewModel,
    authManager: ServerService,

    ) {
    authManager.server(8080) { code ->
        if (code != null) {
            authViewModel.onAction(AuthAction.GetTwitchUser(code))
            //Log.d("AuthCallback", "Authorization code received: $code")
        } else {
            //Log.e("AuthCallback", "Authorization code not found")
        }
        authManager.server(8080) {}.stop(1000, 10000)
    }
}

private fun handleServerGoogle(
    authViewModel: AuthViewModel,
    authManager: ServerService,
) {
    authManager.server(8081) { code ->
        if (code != null) {
            authViewModel.onAction(AuthAction.GetGoogleUser(code))
            //Log.d("AuthCallback", "Authorization code received: $code")
        } else {
            //Log.e("AuthCallback", "Authorization code not found")
        }
        authManager.server(8081) {}.stop(1000, 10000)
    }
}