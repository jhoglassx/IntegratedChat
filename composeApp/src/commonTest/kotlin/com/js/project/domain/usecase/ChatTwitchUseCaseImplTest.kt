package com.js.project.domain.usecase

import com.js.project.data.datasource.SourceEnum
import com.js.project.data.repository.ChatTwitchRepository
import com.js.project.domain.entity.ChatMessageEntity
import com.js.project.domain.entity.UserEntity
import io.kotest.matchers.shouldBe
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.toInstant
import org.junit.After
import org.junit.Before
import kotlin.test.Test


class ChatTwitchUseCaseImplTest {

    @MockK
    private lateinit var chatTwitchRepository: ChatTwitchRepository
    @MockK
    private lateinit var chatTwitchUseCase: ChatTwitchUseCaseImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        chatTwitchUseCase = ChatTwitchUseCaseImpl(chatTwitchRepository)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getTwitchChat should return flow of chat messages`() = runTest {
        // Given
        val userTwitch = UserEntity(
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

        coEvery { chatTwitchRepository.getTwitchChat(userTwitch) } returns flowOf(chatMessagesFlow)

        // When
        val result = chatTwitchUseCase.getTwitchChat(userTwitch)

        // Then
        result.collect { chatMessage ->
            chatMessage shouldBe chatMessagesFlow
        }

        coVerify { chatTwitchRepository.getTwitchChat(userTwitch) }
    }

    @Test
    fun `getTwitchChat should handle empty chat messages`() = runTest {
        // Given
        val userTwitch = UserEntity(
        id = "",
        name = "testUser",
        email = "email",
        displayName = "displayName",
        imageUrl = "imageUrl"
    )
        val emptyChatMessagesFlow: Flow<ChatMessageEntity> = flowOf()

        coEvery { chatTwitchRepository.getTwitchChat(userTwitch) } returns emptyChatMessagesFlow

        // When
        val result = chatTwitchUseCase.getTwitchChat(userTwitch).toList()

        // Then
        result.size shouldBe 0
        coVerify { chatTwitchRepository.getTwitchChat(userTwitch) }
    }
}