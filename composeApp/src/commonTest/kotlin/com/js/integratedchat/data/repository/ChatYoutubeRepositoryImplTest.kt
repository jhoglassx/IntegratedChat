package com.js.integratedchat.data.repository

import com.js.integratedchat.data.datasource.ChatYoutubeDataSource
import com.js.integratedchat.data.entity.ChatMessageEntityRemote
import com.js.integratedchat.data.entity.SourceEnum
import com.js.integratedchat.domain.entity.ChatMessageEntity
import com.js.integratedchat.domain.entity.UserEntity
import com.js.integratedchat.domain.entity.toRemote
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
import org.junit.After
import org.junit.Before
import kotlin.test.Test

class ChatYoutubeRepositoryImplTest {

    @MockK
    private lateinit var chatYoutubeDataSource: ChatYoutubeDataSource

    private lateinit var chatYoutubeRepositoryImpl: ChatYoutubeRepositoryImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        chatYoutubeRepositoryImpl = ChatYoutubeRepositoryImpl(
            chatYoutubeDataSource = chatYoutubeDataSource
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `Given googleUser, when getYouTubeChat is called, then it should return mapped ChatMessageEntity flow`() = runTest {
        // Given
        val googleUser = UserEntity(
            id = "id",
            email = "email",
            name = "name",
            displayName = "displayName",
            imageUrl = "imageUrl"
        )
        val chatMessageEntityRemote = ChatMessageEntityRemote(
            id = "id",
            userId = "userId",
            userName = "userName",
            displayName = "displayName",
            timestamp = null,
            message = "message",
            badges = null,
            emotes = null,
            source = SourceEnum.YOUTUBE,
            channelId = "channelId",
            channelName = "channelName"
        )

        val chatMessageEntity = ChatMessageEntity(
            id = "id",
            userId = "userId",
            userName = "userName",
            displayName = "displayName",
            timestamp = null,
            message = "message",
            badges = null,
            emotes = null,
            source = SourceEnum.YOUTUBE,
            channelId = "channelId",
            channelName = "channelName"
        )

        coEvery {
            chatYoutubeDataSource.getYouTubeChat(googleUser.toRemote())
        } returns flowOf(chatMessageEntityRemote)

        // When
        val result = chatYoutubeRepositoryImpl.getYouTubeChat(googleUser).toList()

        // Then
        result.size shouldBe 1
        result[0] shouldBe chatMessageEntity

        coVerify {
            chatYoutubeDataSource.getYouTubeChat(googleUser.toRemote())
        }
        confirmVerified(chatYoutubeDataSource)
    }

    @Test
    fun `When isLiveStreamActive is called, then it should return the correct live status flow`() = runTest {
        // Given
        val isLive = true
        val expectedFlow = flowOf(isLive)

        coEvery {
            chatYoutubeDataSource.isLiveStreamActive()
        } returns expectedFlow

        // When
        val result = chatYoutubeRepositoryImpl.isLiveStreamActive().toList()

        // Then
        result.size shouldBe 1
        result[0] shouldBe isLive

        coVerify {
            chatYoutubeDataSource.isLiveStreamActive()
        }
        confirmVerified(chatYoutubeDataSource)
    }
}