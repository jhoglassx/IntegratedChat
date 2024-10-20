package com.js.integratedchat.ui.app

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.js.integratedchat.data.entity.SourceEnum
import com.js.integratedchat.domain.entity.FilterOption
import com.js.integratedchat.ui.auth.model.AuthState
import com.js.integratedchat.ui.chat.ChatScreen
import com.js.integratedchat.ui.navigation.ChatNavigation
import org.jetbrains.compose.resources.painterResource

@Composable
fun App(
    onSignInGoogle: () -> Unit,
    onSignInTwitch: () -> Unit,
    authState: AuthState,
) {
    MaterialTheme {
        val navController = rememberNavController()
        var selectedFilter by remember { mutableStateOf(FilterOption.ALL) }


        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                topBar(
                    screenName = ChatNavigation.route,
                    onSignInTwitch = onSignInTwitch,
                    onSignInGoogle = onSignInGoogle,
                    authState = authState,
                    onFilterSelected = { filterOption ->
                        selectedFilter = filterOption
                    }
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = ChatNavigation.route
            ) {
                composable(route = ChatNavigation.route) {
                    ChatScreen(
                        innerPadding = innerPadding,
                        authState = authState,
                        selectedFilter = selectedFilter
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun topBar(
    authState: AuthState,
    onSignInGoogle: () -> Unit,
    onSignInTwitch: () -> Unit,
    screenName: String,
    onFilterSelected: (FilterOption) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var filterExpanded by remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        title = { Text(screenName) },
        actions = {
            Icon(
                painter = painterResource(SourceEnum.YOUTUBE.icon),
                contentDescription = "YouTube Logged In",
                tint = if(authState.userGoggle != null) Color(0xFFFF0000)  else Color.DarkGray,
                modifier = Modifier
                    .height(24.dp)
                    .clickable {
                        onSignInGoogle()
                    }
            )
            Spacer(modifier = Modifier.padding(2.dp))
            Icon(
                painter = painterResource(SourceEnum.TWITCH.icon),
                tint = if(authState.userTwitch != null) Color(0xFF9146FF) else Color.DarkGray,
                contentDescription = "Twitch Logged In",
                modifier = Modifier
                    .height(24.dp)
                    .clickable {
                        onSignInTwitch()
                    }
            )

            Box {

                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    if (authState.userGoggle == null) {
                        DropdownMenuItem(
                            text = { Text("Login YouTube") },
                            onClick = { onSignInGoogle() }
                        )
                    }

                    if (authState.userTwitch == null) {
                        DropdownMenuItem(
                            text = { Text("Login Twitch") },
                            onClick = { onSignInTwitch() }
                        )
                    }

                    if (authState.userTwitch != null || authState.userGoggle == null) {
                        DropdownMenuItem(
                            text = { Text("logout") },
                            onClick = { }
                        )
                    }
                }
            }

            Box {
                IconButton(onClick = { filterExpanded = true }) {
                    Icon(Icons.Filled.FilterList, contentDescription = "Filter")
                }
                DropdownMenu(
                    expanded = filterExpanded,
                    onDismissRequest = { filterExpanded = false }
                ) {
                    FilterOption.entries.forEach { option ->
                        DropdownMenuItem(
                            onClick = {
                                onFilterSelected(option) // Chama o callback com a opção selecionada
                                filterExpanded = false
                            },
                            text = { Text(option.name) },
                        )
                    }
                }
            }
        }
    )
}