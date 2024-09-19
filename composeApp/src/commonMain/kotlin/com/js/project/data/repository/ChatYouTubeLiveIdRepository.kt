package com.js.project.data.repository

import kotlinx.coroutines.flow.Flow

interface  ChatYouTubeLiveIdRepository {
    suspend fun getYouTubeLiveChatId(
        channelId: String
    ): Flow<String>
}


