package com.js.integratedchat.data.repository

import com.js.integratedchat.data.datasource.ChatYouTubeLiveIdDataSource
import kotlinx.coroutines.flow.Flow

class ChatYouTubeLiveIdRepositoryImpl(
    private val chatYouTubeLiveIdDataSource: ChatYouTubeLiveIdDataSource
): ChatYouTubeLiveIdRepository {
    override suspend fun getYouTubeLiveChatId(
        channelId: String
    ): Flow<String> = chatYouTubeLiveIdDataSource.getYouTubeLiveChatId(channelId)
}


