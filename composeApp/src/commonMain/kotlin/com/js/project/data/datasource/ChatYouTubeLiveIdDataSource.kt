package com.js.project.data.datasource

import kotlinx.coroutines.flow.Flow

interface  ChatYouTubeLiveIdDataSource {
    suspend fun getYouTubeLiveChatId(
        channelId: String
    ): Flow<String>
}


