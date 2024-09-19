package com.js.project.ui.chat

import com.js.project.domain.entity.ChatMessageEntity
import com.js.project.domain.entity.UserEntity
import com.js.project.domain.usecase.ChatTwitchUseCase
import com.js.project.domain.usecase.ChatYoutubeUseCase
import com.js.project.provider.DispatcherProvider
import com.js.project.ui.chat.model.ChatAction
import io.kotest.matchers.shouldBe
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.toInstant
import org.junit.After
import org.junit.Before
import kotlin.test.Test


class ChatViewModelTest {

    @MockK
    private lateinit var chatTwitchUseCase: ChatTwitchUseCase
    @MockK
    private lateinit var chatYoutubeUseCase: ChatYoutubeUseCase
    @MockK
    private lateinit var dispatcherProvider: DispatcherProvider

    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testScheduler)

    private lateinit var viewModel: ChatViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        coEvery { dispatcherProvider.MAIN } returns testDispatcher

        Dispatchers.setMain(testDispatcher)

        viewModel = ChatViewModel(
            chatTwitchUseCase = chatTwitchUseCase,
            chatYoutubeUseCase = chatYoutubeUseCase,
            dispatcherProvider = dispatcherProvider
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `onAction with LoadMessages should update uiState with combined chat messages`() = runTest {
        // Given
        val userTwitch = UserEntity(
            id = "userTwitch_id",
            email = "userTwitch_email",
            name = "userTwitch_name",
            displayName = "userTwitch_displayName",
            imageUrl = "userTwitch_imageIrl"
        )
        val userYouTube = UserEntity(
            id = "userYouTube_id",
            email = "userYouTube_email",
            name = "userYouTube_name",
            displayName = "userYouTube_displayName",
            imageUrl = "userYouTube_imageIrl"
        )
        val twitchMessage = ChatMessageEntity(
            id = "twitchMessage_id",
            displayName = "twitchMessage_displayName",
            message = "twitchMessage_message",
            source = "twitchMessage_source",
            timestamp = "2024-09-11T16:01:11.042Z".toInstant()
        )
        val youtubeMessage = ChatMessageEntity(
            id = "youtubeMessage_id",
            displayName = "youtubeMessage_displayName",
            message = "youtubeMessage_message",
            source = "youtubeMessage_source",
            timestamp = "2024-09-11T16:02:11.042Z".toInstant()
        )

        coEvery {
            chatTwitchUseCase.getTwitchChat(any())
        } returns flowOf(twitchMessage)

        coEvery {
            chatYoutubeUseCase.getYouTubeChat(any())
        } returns flowOf(youtubeMessage)

        // When
        viewModel.onAction(ChatAction.LoadMessages(userTwitch, userYouTube))

        advanceUntilIdle()

        // Then
        val expectedMessages = listOf(twitchMessage, youtubeMessage).sortedBy { it.timestamp }
        val actualMessages = viewModel.uiState.first().chatMessages

        actualMessages shouldBe expectedMessages
    }
}