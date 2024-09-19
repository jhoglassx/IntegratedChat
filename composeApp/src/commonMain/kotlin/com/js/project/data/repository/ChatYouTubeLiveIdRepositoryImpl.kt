package com.js.project.data.repository

import com.js.project.data.datasource.ChatYouTubeLiveIdDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last

class ChatYouTubeLiveIdRepositoryImpl(
    private val chatYouTubeLiveIdDataSource: ChatYouTubeLiveIdDataSource
): ChatYouTubeLiveIdRepository {
    override suspend fun getYouTubeLiveChatId(
        channelId: String
    ): Flow<String> = chatYouTubeLiveIdDataSource.getYouTubeLiveChatId(channelId)
}


