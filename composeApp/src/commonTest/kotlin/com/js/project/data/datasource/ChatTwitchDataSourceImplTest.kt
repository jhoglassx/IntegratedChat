package com.js.project.data.datasource


import Constants.TWITCH_TOKEN
import com.js.project.data.entity.ChatMessageEntityRemote
import com.js.project.data.entity.EmoteRemoteEntity
import com.js.project.provider.DispatcherProvider
import com.js.project.service.TwitchChatService
import io.kotest.matchers.shouldBe
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Instant
import org.junit.After
import org.junit.Before
import kotlin.test.Test

@ExperimentalCoroutinesApi
class ChatTwitchDataSourceImplTest {

    @MockK
    private lateinit var dispatcherProvider: DispatcherProvider
    @MockK
    private lateinit var twitchChatService: TwitchChatService
    private lateinit var chatTwitchDataSource: ChatTwitchDataSourceImpl
    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testScheduler)

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        dispatcherProvider = mockk(relaxed = true)
        every { dispatcherProvider.IO } returns testDispatcher

        Dispatchers.setMain(testDispatcher)

        chatTwitchDataSource = ChatTwitchDataSourceImpl(twitchChatService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `getTwitchChat should emit parsed chat messages`() = runTest {
        // Given
        val channel = "testChannel"
        val twitchMessage = "id=123;user-id=456;login=testUser;display-name=TestUser;badges=subscriber/1;color=#1E90FF;emotes=25:0-4;tmi-sent-ts=1234567890;room-id=789 :testUser!testUser@testUser.tmi.twitch.tv PRIVMSG #$channel :Hello, World!"
        val chatMessageEntityRemote = ChatMessageEntityRemote(
            id = "123",
            userId = "456",
            userName = "testUser",
            displayName = "TestUser",
            timestamp = Instant.fromEpochMilliseconds(1234567890),
            message = "Hello, World!",
            badges = mapOf("subscriber" to "1"),
            emotes = mapOf("25" to listOf(EmoteRemoteEntity(0, 4))),
            source = "Twitch",
            channelId = "789",
            channelName = channel
        )
        coEvery {
            twitchChatService.getTwitchChatMessages(
                channel = channel,
                twitchUsername = channel,
                twitchToken = TWITCH_TOKEN
            )
        } returns flowOf(twitchMessage)

        // When
        val result = mutableListOf<ChatMessageEntityRemote>()
        chatTwitchDataSource.getTwitchChat(channel).collect {
            result.add(it)
        }

        // Then
        result.size shouldBe 1
        result[0] shouldBe chatMessageEntityRemote
        coVerify (exactly = 1) {
            twitchChatService.getTwitchChatMessages(
                channel = channel,
                twitchUsername = channel,
                twitchToken = TWITCH_TOKEN
            )
        }
    }

    @Test
    fun `getTwitchChat should not emit when message is invalid`() = runTest {
        // Given
        val channel = "testChannel"
        val invalidMessage = "invalid message"
        coEvery {
            twitchChatService.getTwitchChatMessages(
                channel = channel,
                twitchUsername = channel,
                twitchToken = TWITCH_TOKEN
            )
        } returns flowOf(invalidMessage)

        // When
        val result = mutableListOf<ChatMessageEntityRemote>()
        chatTwitchDataSource.getTwitchChat(channel).collect {
            result.add(it)
        }

        // Then
        result.size shouldBe 0
        coVerify {
            twitchChatService.getTwitchChatMessages(
                channel = channel,
                twitchUsername = channel,
                twitchToken = TWITCH_TOKEN
            )
        }
    }
}
