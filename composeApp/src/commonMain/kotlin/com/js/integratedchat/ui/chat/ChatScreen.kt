package com.js.integratedchat.ui.chat

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
import androidx.compose.material.Text
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil3.compose.AsyncImage
import com.js.integratedchat.domain.entity.ChatMessageEntity
import com.js.integratedchat.domain.entity.processMessage
import com.js.integratedchat.ui.auth.model.AuthState
import com.js.integratedchat.ui.chat.model.ChatAction
import com.js.integratedchat.ui.chat.model.ChatState
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
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
        horizontalAlignment = Alignment.CenterHorizontally,
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

@Composable
fun ChatMessageItem(
    chatMessage: ChatMessageEntity
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Icon(
            painter = painterResource(chatMessage.source.icon),
            contentDescription = chatMessage.source.description,
            tint = Color.White.copy(alpha = 0.3f),
            modifier = Modifier
                .padding(vertical = 2.dp, horizontal = 10.dp)
                .height(48.dp)
                .align(Alignment.TopEnd)
                .zIndex(1f)
        )

        Column (
            modifier = Modifier
                .fillMaxWidth()
                .background(chatMessage.source.color)
                .padding(start = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .background(Color.DarkGray)
                    .padding(4.dp)
                    .fillMaxWidth()
            ) {
                chatMessage.badges?.forEach { badge ->
                    AsyncImage(
                        model = badge.url,
                        contentDescription = null,
                        modifier = Modifier
                            .size(16.dp)
                            .background(Color.Transparent),
                        contentScale = ContentScale.Crop,
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = chatMessage.displayName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    lineHeight = 14.sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = ":",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    lineHeight = 14.sp,
                    color = Color.White
                )
            }

            Row(
                modifier = Modifier
                    .background(Color.DarkGray)
                    .fillMaxWidth()
                    .padding(vertical = 4.dp, horizontal = 8.dp)
            ) {
                ChatMessageView(chatMessage)
            }
        }
    }


}
@Composable
fun ChatMessageView(
    chatMessage: ChatMessageEntity
) {
    val processedMessage = chatMessage.processMessage()
    processedMessage.forEach { part ->
        part.text?.let { text ->
            Text(
                text = text,
                fontSize = 14.sp,
                color = Color.White,
                modifier = Modifier
                    .padding(end = 2.dp)
            )
        }
        part.imageUrl?.let { imageUrl ->
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(26.dp)
                    .background(Color.Transparent),
                contentScale = ContentScale.Crop,
            )
        }
    }
}