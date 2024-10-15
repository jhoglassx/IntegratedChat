package com.js.project.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation.Vertical
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.js.project.data.datasource.SourceEnum
import com.js.project.domain.entity.ChatMessageEntity
import com.js.project.domain.entity.processMessage
import com.js.project.ui.auth.model.AuthState
import com.js.project.ui.chat.model.ChatAction
import com.js.project.ui.chat.model.ChatState
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

@Composable
fun ChatScreen(
    innerPadding : PaddingValues,
    authState: AuthState
) {

    val viewModel: ChatViewModel = koinInject ()
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(authState.userGoggle, authState.userTwitch) {
        viewModel.onAction(ChatAction.LoadMessages(
            userYouTube = authState.userGoggle,
            userTwitch = authState.userTwitch
        ))
    }
    ChatMessage(
        innerPadding = innerPadding,
        chatState = uiState
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
                    Spacer(modifier = Modifier.padding(2.dp))
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChatMessageItem(
    chatMessage: ChatMessageEntity
) {
    Box(
        modifier = Modifier
            .background(chatMessage.source.color)
            .fillMaxSize()
            .padding(start = 8.dp)
    ) {
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.LightGray)
                .padding(4.dp)
        ) {
            chatMessage.badges?.forEach { badge ->
                AsyncImage(
                    model = badge.url,
                    contentDescription = null,
                    modifier = Modifier.padding(4.dp),
                    contentScale = ContentScale.Crop,
                )
            }
            Text(
                text = chatMessage.displayName,
                style = MaterialTheme.typography.body1.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
            )
            ChatMessageView(chatMessage)
        }
    }
}

@Composable
fun ChatMessageView(chatMessage: ChatMessageEntity) {
    val processedMessage = chatMessage.processMessage()
    processedMessage.forEach { part ->
        part.text?.let {
            Text(
                text = it,
                overflow = TextOverflow.Clip,
                style = MaterialTheme.typography.body1
            )
        }
        part.imageUrl?.let { imageUrl ->
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier.size(26.dp),
                contentScale = ContentScale.Crop,
            )
        }
    }
}

@Preview
@Composable
fun ChatMessagePreview1(){
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