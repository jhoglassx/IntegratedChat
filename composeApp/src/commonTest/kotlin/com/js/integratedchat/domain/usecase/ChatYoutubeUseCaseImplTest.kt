package com.js.integratedchat.domain.usecase

import com.js.integratedchat.data.datasource.SourceEnum
import com.js.integratedchat.data.repository.ChatYouTubeLiveIdRepository
import com.js.integratedchat.data.repository.ChatYoutubeRepository
import com.js.integratedchat.domain.entity.ChatMessageEntity
import com.js.integratedchat.domain.entity.UserEntity
import io.kotest.matchers.shouldBe
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.toInstant
import org.junit.After
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class ChatYoutubeUseCaseImplTest {

    @MockK
    private lateinit var chatYouTubeLiveIdRepository: ChatYouTubeLiveIdRepository
    @MockK
    private lateinit var chatYoutubeRepository: ChatYoutubeRepository

    private lateinit var chatYoutubeUseCase: ChatYoutubeUseCaseImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        chatYoutubeUseCase = ChatYoutubeUseCaseImpl(chatYouTubeLiveIdRepository, chatYoutubeRepository)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getYouTubeChat should return chat messages when live chat ID is available`() = runTest {
        // Given
        val userGoogle = UserEntity(
            id = "",
            name = "testUser",
            email = "email",
            displayName = "displayName",
            imageUrl = "imageUrl"
        )
        val chatMessagesFlow = ChatMessageEntity(
            id = "",
            message = "testMessage",
            displayName = "",
            source = SourceEnum.YOUTUBE,
            timestamp = "2024-09-11T16:01:11.042Z".toInstant()
        )
        val liveChatId = "liveChatId"

        coEvery {
            chatYouTubeLiveIdRepository.getYouTubeLiveChatId(userGoogle.id)
        } returns flow { emit(liveChatId) }
        coEvery {
            chatYoutubeRepository.getYouTubeChat(userGoogle)
        } returns flow { emit(chatMessagesFlow) }

        coEvery {
            chatYoutubeRepository.isLiveStreamActive()
        } returns flow { emit(true) }

        // When
        val result = chatYoutubeUseCase.getYouTubeChat(userGoogle).last()

        // Then
        result shouldBe chatMessagesFlow
        coVerify(exactly = 1) {
            chatYoutubeRepository.isLiveStreamActive()
            chatYouTubeLiveIdRepository.getYouTubeLiveChatId(userGoogle.id)
            chatYoutubeRepository.getYouTubeChat(userGoogle)
        }
    }

    @Test
    fun `getYouTubeChat should not emit anything when live chat ID is null`() = runTest {
        // Given
        val userGoogle = UserEntity(
            id = "",
            name = "testUser",
            email = "email",
            displayName = "displayName",
            imageUrl = "imageUrl"
        )

        coEvery {
            chatYouTubeLiveIdRepository.getYouTubeLiveChatId(userGoogle.id)
        } returns flow { emit("") }

        coEvery {
            chatYoutubeRepository.getYouTubeChat(userGoogle)
        } returns flow { }

        coEvery {
            chatYoutubeRepository.isLiveStreamActive()
        } returns flow { emit(true) }

        // When
        val result = chatYoutubeUseCase.getYouTubeChat(userGoogle).toList()

        // Then
        result.size shouldBe 0
        coVerify { chatYouTubeLiveIdRepository.getYouTubeLiveChatId(userGoogle.id) }
        coVerify(exactly = 0) { chatYoutubeRepository.getYouTubeChat(any()) }
    }

    @Test
    fun `getYouTubeChat should not return chat messages when live is not available`() = runTest {
        // Given
        val userGoogle = UserEntity(
            id = "",
            name = "testUser",
            email = "email",
            displayName = "displayName",
            imageUrl = "imageUrl"
        )
        val chatMessagesFlow = ChatMessageEntity(
            id = "",
            message = "testMessage",
            displayName = "",
            source = SourceEnum.TWITCH,
            timestamp = "2024-09-11T16:01:11.042Z".toInstant()
        )
        val liveChatId = "liveChatId"

        coEvery {
            chatYouTubeLiveIdRepository.getYouTubeLiveChatId(userGoogle.id)
        } returns flow { emit(liveChatId) }
        coEvery {
            chatYoutubeRepository.getYouTubeChat(userGoogle)
        } returns flow { emit(chatMessagesFlow) }

        coEvery {
            chatYoutubeRepository.isLiveStreamActive()
        } returns flow { emit(false) }

        // When
        val result = chatYoutubeUseCase.getYouTubeChat(userGoogle).toList()

        // Then
        result.size shouldBe 0
        coVerify(exactly = 1) {
            chatYoutubeRepository.isLiveStreamActive()
        }

        coVerify(exactly = 0) {
            chatYouTubeLiveIdRepository.getYouTubeLiveChatId(userGoogle.id)
            chatYoutubeRepository.getYouTubeChat(userGoogle)
        }
    }
}