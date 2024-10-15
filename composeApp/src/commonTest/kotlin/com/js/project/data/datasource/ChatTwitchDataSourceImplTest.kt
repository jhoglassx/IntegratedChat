package com.js.project.data.datasource


import Constants.TWITCH_TOKEN
import com.js.project.data.entity.ChatMessageEntityRemote
import com.js.project.data.entity.EmotePositionRemoteEntity
import com.js.project.data.entity.EmoteRemoteEntity
import com.js.project.domain.entity.UserEntity
import com.js.project.provider.BadgeCache
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
import kotlinx.coroutines.flow.flow
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
    @MockK
    private lateinit var badgeCache: BadgeCache
    private lateinit var chatTwitchDataSource: ChatTwitchDataSourceImpl
    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testScheduler)

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        dispatcherProvider = mockk(relaxed = true)
        every { dispatcherProvider.IO } returns testDispatcher

        Dispatchers.setMain(testDispatcher)

        chatTwitchDataSource = ChatTwitchDataSourceImpl(
            badgeCache = badgeCache,
            twitchChatService = twitchChatService
        )
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
        val twitchMessage = "id=123;user-id=456;login=testChannel;display-name=testChannel;badges=subscriber/1;color=#1E90FF;emotes=25:0-4;tmi-sent-ts=1234567890;room-id=789 :testUser!testUser@testUser.tmi.twitch.tv PRIVMSG #$channel :Hello, World!"
        val chatMessageEntityRemote = ChatMessageEntityRemote(
            id = "123",
            userId = "456",
            userName = channel,
            displayName = channel,
            timestamp = Instant.fromEpochMilliseconds(1234567890),
            message = "Hello, World!",
            source = SourceEnum.TWITCH,
            channelId = "789",
            channelName = channel,
            emotes = listOf(
                EmoteRemoteEntity(
                    emoteId = "25",
                    imgUrl = "",
                    positions = listOf(
                        EmotePositionRemoteEntity(0,4)
                    )
                )
            )
        )
        val user = UserEntity(
            id = "",
            name = channel,
            email = "email",
            displayName = "displayName",
            imageUrl = "imageUrl"
        )

        coEvery {
            badgeCache.start()
        } returns Unit

        coEvery {
            twitchChatService.getTwitchChatMessages(
                channel = channel,
                twitchUsername = channel,
                twitchToken = TWITCH_TOKEN
            )
        } returns flowOf(twitchMessage)

        // When
        val result = mutableListOf<ChatMessageEntityRemote>()
        chatTwitchDataSource.getTwitchChat(user).collect {
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
        val user = UserEntity(
            id = "",
            name = "testUser",
            email = "email",
            displayName = "displayName",
            imageUrl = "imageUrl"
        )

        coEvery { badgeCache.start() } returns Unit
        coEvery {
            twitchChatService.getTwitchChatMessages(
                twitchUsername = user.name,
                channel = user.name,
                twitchToken = TWITCH_TOKEN
            )
        } returns flow {
            emit(null.toString())
        }

        // When
        val result = mutableListOf<ChatMessageEntityRemote>()
        val flow = chatTwitchDataSource.getTwitchChat(user)
        flow.collect { result.add(it) }

        // Then
        result.size shouldBe 0
        coVerify {
            badgeCache.start()
        }
        coVerify {
            twitchChatService.getTwitchChatMessages(
                twitchUsername = user.name,
                channel = user.name,
                twitchToken = TWITCH_TOKEN
            )
        }
    }
}
