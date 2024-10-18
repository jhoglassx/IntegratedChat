package com.js.integratedchat.data.repository

import com.js.integratedchat.data.datasource.ChatTwitchDataSource
import com.js.integratedchat.data.entity.ChatMessageEntityRemote
import com.js.integratedchat.data.entity.SourceEnum
import com.js.integratedchat.domain.entity.ChatMessageEntity
import com.js.integratedchat.domain.entity.UserEntity
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

class ChatYouTubeLiveIdRepositoryImplTest() {
    @MockK
    private lateinit var chatTwitchDataSource: ChatTwitchDataSource

    private lateinit var chatTwitchRepositoryImpl: ChatTwitchRepositoryImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        chatTwitchRepositoryImpl = ChatTwitchRepositoryImpl(
            chatTwitchDataSource = chatTwitchDataSource
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `Given userEntity, when getTwitchChat is called, then it should return mapped ChatMessageEntity flow`() = runTest {
        // Given
        val userEntity = UserEntity(
            id = "id",
            name = "name",
            displayName = "displayName",
            imageUrl = "imageUrl",
            email = "email"
        )
        val chatMessageEntityRemote = ChatMessageEntityRemote(
            id = "id",
            userId = "userId",
            userName = "userName",
            displayName = "displayName",
            timestamp = Instant.DISTANT_PAST,
            message = "message",
            badges = listOf(),
            emotes = listOf(),
            source = SourceEnum.TWITCH,
            channelId = "channelId",
            channelName = "channelName"
        )

        val chatMessageEntity = ChatMessageEntity(
            id = "id",
            userId = "userId",            userName = "userName",
            displayName = "displayName",
            timestamp = Instant.DISTANT_PAST,
            message = "message",
            badges = listOf(),
            emotes = listOf(),
            source = SourceEnum.TWITCH,
            channelId = "channelId",
            channelName = "channelName"
        )

        coEvery {
            chatTwitchDataSource.getTwitchChat(userEntity)
        } returns flowOf(chatMessageEntityRemote)

        // When
        val result = chatTwitchRepositoryImpl.getTwitchChat(userEntity).toList()

        // Then
        result.size shouldBe 1
        result[0] shouldBe chatMessageEntity

        coVerify {
            chatTwitchDataSource.getTwitchChat(userEntity)
        }
        confirmVerified(chatTwitchDataSource)
    }
}