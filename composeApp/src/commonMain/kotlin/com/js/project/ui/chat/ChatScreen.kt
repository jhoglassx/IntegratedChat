package com.js.project.ui.chat


import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation.Vertical
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.js.project.domain.entity.ChatMessageEntity
import com.js.project.domain.entity.UserEntity
import com.js.project.ui.auth.model.AuthState
import com.js.project.ui.chat.model.ChatAction
import com.js.project.ui.chat.model.ChatState
import com.js.project.ui.navigation.ChatNavigation
import integratedchat.composeapp.generated.resources.Res
import integratedchat.composeapp.generated.resources.ic_twitch
import integratedchat.composeapp.generated.resources.ic_youtube
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

@Composable
fun ChatScreen(
    authState: AuthState,
    onSignInGoogle: () -> Unit,
    onSignInTwitch: () -> Unit,
    navController: NavHostController,
) {

    val viewModel: ChatViewModel = koinInject ()
    val uiState by viewModel.uiState.collectAsState()


    LaunchedEffect(authState.userTwitch, authState.userTwitch) {
        viewModel.onAction(ChatAction.LoadMessages(
            userYouTube = authState.userGoggle,
            userTwitch = authState.userTwitch
        ))
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            topBar(
                authState = authState,
                onSignInGoogle = onSignInGoogle,
                onSignInTwitch = onSignInTwitch,
                screanName = ChatNavigation.route
            )
        }
    ) { innerPadding ->
        ChatMessage(
            innerPadding = innerPadding,
            chatState = uiState
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun topBar(
    authState: AuthState,
    onSignInGoogle: () -> Unit,
    onSignInTwitch: () -> Unit,
    screanName: String,
) {
    var expanded by remember { mutableStateOf(false) }
    CenterAlignedTopAppBar(
        title = { Text(screanName) },
        actions = {
            Icon(
                painter = painterResource(Res.drawable.ic_youtube),
                contentDescription = "YouTube Logged In",
                tint = if(authState.userGoggle != null) Color.Green else Color.DarkGray,
                modifier = Modifier
                    .size(25.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color.Gray)
                    .padding(horizontal = 0.dp, vertical = 2.dp)
            )
            Spacer(modifier = Modifier.padding(2.dp))
            Icon(
                painter = painterResource(Res.drawable.ic_twitch),
                tint = if(authState.userTwitch != null) Color.Green else Color.DarkGray,
                contentDescription = "Twitch Logged In",
                modifier = Modifier
                    .size(25.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color.Gray)
                    .padding(horizontal = 0.dp, vertical = 2.dp)
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
                            text = {Text("Login YouTube")},
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
        }
    )
}

@Composable
fun ChatMessage(
    innerPadding: PaddingValues,
    chatState: ChatState
) {
    val chatMessages =  chatState.chatMessages

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    
    val isAtBottom by remember {
        derivedStateOf {
            val lastVisibleItemIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisibleItemIndex == listState.layoutInfo.totalItemsCount - 2
        }
    }

    LaunchedEffect(chatMessages) {
        if (isAtBottom) {
            coroutineScope.launch {
                listState.animateScrollToItem(chatMessages.size - 2)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                items(chatMessages) { message ->
                    ChatMessageItem(chatMessage = message)
                }
            }

            val scrollState = rememberScrollState()
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .width(8.dp)
                    .fillMaxHeight()
                    .background(Color.Gray)
                    .scrollable(scrollState, Vertical)
            ) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .width(8.dp)
                        .height((listState.layoutInfo.totalItemsCount * 16).dp)
                        .background(Color.DarkGray)
                )
            }
        }
    }
}

@Composable
fun ChatMessageItem(
    chatMessage: ChatMessageEntity
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colors.surface)
            .padding(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = chatMessage.source ?: "",
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = ":",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = chatMessage.displayName ?: "",
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = ":",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = chatMessage.message ?: "",
                    fontSize = 16.sp
                )
            }
        }
    }
}


@Composable
@Preview
fun ChatScreenPreview(){
    val navController = rememberNavController()
    ChatScreen(
        authState = AuthState(
            authGoogleIntent = "",
            authTwitchIntent = "",
            userTwitch = UserEntity(
                id = "userTwitch_1",
                name = "userTwitch_name",
                email = "userTwitch_email",
                displayName = "userTwitch_displayName",
                imageUrl = "userTwitch_imageUrl"
            ),
            userGoggle = UserEntity(
                id = "userGoggle_1",
                name = "userGoggle_name",
                email = "userGoggle_email",
                displayName = "userGoggle_displayName",
                imageUrl = "userGoggle_imageUrl"
            ),
            twitchCode = ""
        ),
        onSignInGoogle = {},
        onSignInTwitch = {},
        navController = navController,
    )
}