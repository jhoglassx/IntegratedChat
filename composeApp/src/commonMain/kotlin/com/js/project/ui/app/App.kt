package com.js.project.ui.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.js.project.ui.auth.model.AuthState
import com.js.project.ui.chat.ChatScreen
import com.js.project.ui.chat.topBar
import com.js.project.ui.navigation.ChatNavigation

@Composable
fun App(
    onSignInGoogle: () -> Unit,
    onSignInTwitch: () -> Unit,
    authState: AuthState,
) {
    MaterialTheme {
        val navController = rememberNavController()

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                topBar(
                    screanName = ChatNavigation.route,
                    onSignInTwitch = onSignInTwitch,
                    onSignInGoogle = onSignInGoogle,
                    authState = authState
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = ChatNavigation.route
            ) {
                composable(
                    route = ChatNavigation.route
                ) {
                    ChatScreen(
                        navController = navController,
                        authState = authState,
                        onSignInGoogle = onSignInGoogle,
                        onSignInTwitch = onSignInTwitch
                    )
                }
            }
        }
    }
}
