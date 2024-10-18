package com.js.integratedchat.data.repository

import com.js.integratedchat.data.datasource.ChatTwitchDataSource
import com.js.integratedchat.data.datasource.ChatYouTubeLiveIdDataSource
import com.js.integratedchat.data.entity.ChatMessageEntityRemote
import com.js.integratedchat.data.entity.SourceEnum
import com.js.integratedchat.domain.entity.ChatMessageEntity
import com.js.integratedchat.domain.entity.UserEntity
import com.js.integratedchat.provider.BadgeCache
import io.kotest.matchers.shouldBe
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Instant
import org.junit.After
import org.junit.Before
import org.junit.Test

class ChatTwitchRepositoryImplTest {

    @MockK
    private lateinit var chatYouTubeLiveIdDataSource: ChatYouTubeLiveIdDataSource

    private lateinit var chatYouTubeLiveIdRepository: ChatYouTubeLiveIdRepositoryImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        chatYouTubeLiveIdRepository = ChatYouTubeLiveIdRepositoryImpl(
            chatYouTubeLiveIdDataSource = chatYouTubeLiveIdDataSource
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `Given channelId, when getYouTubeLiveChatId is called, then it should return the correct LiveChatId flow`() = runTest {
        // Given
        val channelId = "channel123"
        val liveChatId = "liveChatId123"
        val expectedFlow = flowOf(liveChatId)

        coEvery {
            chatYouTubeLiveIdDataSource.getYouTubeLiveChatId(channelId)
        } returns expectedFlow

        // When
        val result = chatYouTubeLiveIdRepository.getYouTubeLiveChatId(channelId).toList()

        // Then
        result.size shouldBe 1
        result[0] shouldBe liveChatId

        coVerify { chatYouTubeLiveIdDataSource.getYouTubeLiveChatId(channelId) }
        confirmVerified(chatYouTubeLiveIdDataSource)
    }
}