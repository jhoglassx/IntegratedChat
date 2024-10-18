package com.js.integratedchat.data.datasource

import Constants.GOOGLE_LIVE_CHAT_ID
import Constants.GOOGLE_TOKEN
import com.js.integratedchat.data.entity.AuthorDetails
import com.js.integratedchat.data.entity.ChatItem
import com.js.integratedchat.data.entity.ChatMessageEntityRemote
import com.js.integratedchat.data.entity.ChatYoutubeResponse
import com.js.integratedchat.data.entity.LiveBroadcastItem
import com.js.integratedchat.data.entity.LiveBroadcastStatus
import com.js.integratedchat.data.entity.LiveBroadcastsResponse
import com.js.integratedchat.data.entity.Message
import com.js.integratedchat.data.entity.TokenResponseRemoteEntity
import com.js.integratedchat.data.entity.UserRemoteEntity
import com.js.integratedchat.data.entity.toRemote
import com.js.integratedchat.provider.DispatcherProvider
import com.js.integratedchat.service.ApiService
import io.kotest.matchers.shouldBe
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChatYoutubeDataSourceImplTest {

    @MockK
    private lateinit var apiService: ApiService
    @MockK
    private lateinit var httpResponse: HttpResponse

    private lateinit var dispatcherProvider: DispatcherProvider

    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testScheduler)

    private lateinit var chatYoutubeDataSource: ChatYoutubeDataSourceImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        dispatcherProvider = mockk(relaxed = true)
        every { dispatcherProvider.IO } returns testDispatcher

        Dispatchers.setMain(testDispatcher)

        chatYoutubeDataSource = ChatYoutubeDataSourceImpl(dispatcherProvider, apiService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `given successful response, when getYouTubeChat called, then should emit chat messages`() = runTest {
        // Given
        val userRemoteEntity = UserRemoteEntity(
            displayName = "displayName",
            id = "id",
            imageUrl = "https://test.com/image.jpg",
            name = "name",
            email = "email"
        )
        val chatYoutubeResponse = ChatYoutubeResponse(
            items = listOf(
                ChatItem(
                    id = "id",
                    message = Message(
                        publishedAt = "2021-09-01T00:00:00Z",
                        displayMessage = "messageText"
                    ),
                    authorDetails = AuthorDetails(
                        displayName = "authorDisplayName",
                        channelId = "authorChannelId"
                    )
                )
            ),
            nextPageToken = "nextPageToken"
        )

        coEvery {
            apiService.request(
                method = HttpMethod.Get,
                url = "https://www.googleapis.com/youtube/v3/liveChat/messages",
                headers = mapOf("Authorization" to "Bearer $GOOGLE_TOKEN"),
                queryParams = mapOf(
                    "liveChatId" to GOOGLE_LIVE_CHAT_ID,
                    "part" to "snippet,authorDetails",
                    "pageToken" to ""
                )
            )
        } returns httpResponse
        every { httpResponse.status } returns HttpStatusCode.OK
        coEvery { httpResponse.body<ChatYoutubeResponse>() } returns chatYoutubeResponse



        val flow = chatYoutubeDataSource.getYouTubeChat(userRemoteEntity)

        // When
        val emittedMessages = flow.toList()

        // Then
        emittedMessages.size shouldBe chatYoutubeResponse.items?.size
        emittedMessages.forEachIndexed { index, message ->
            message shouldBe (chatYoutubeResponse.items?.get(index)
                ?.toRemote(userRemoteEntity, emptyList())
            )
        }

        coVerify {
            apiService.request(
                method = HttpMethod.Get,
                url = "https://www.googleapis.com/youtube/v3/liveChat/messages",
                headers = mapOf("Authorization" to "Bearer $GOOGLE_TOKEN"),
                queryParams = mapOf(
                    "liveChatId" to GOOGLE_LIVE_CHAT_ID,
                    "part" to "snippet,authorDetails",
                    "pageToken" to ""
                )
            )
        }
    }

    @Test
    fun `given error response, when getYouTubeChat called, then should log error and not emit messages`() = runTest {
        // Given
        val userRemoteEntity = UserRemoteEntity(
            displayName = "displayName",
            id = "id",
            imageUrl = "https://test.com/image.jpg",
            name = "name",
            email = "email"
        )

        coEvery {
            apiService.request(
                method = HttpMethod.Get,
                url = "https://www.googleapis.com/youtube/v3/liveChat/messages",
                headers = mapOf("Authorization" to "Bearer $GOOGLE_TOKEN"),
                queryParams = mapOf(
                    "liveChatId" to GOOGLE_LIVE_CHAT_ID,
                    "part" to "snippet,authorDetails",
                    "pageToken" to ""
                )
            )
        } returns httpResponse

        every { httpResponse.status } returns HttpStatusCode.BadRequest

        mockkStatic("kotlinx.coroutines.DelayKt")
        coEvery { delay(1) } just Runs

        // When
        val flow = chatYoutubeDataSource.getYouTubeChat(userRemoteEntity).take(1)

        // Then
        val emittedMessages = flow.toList()

        emittedMessages.size shouldBe 0
        coVerify {
            apiService.request(
                method = HttpMethod.Get,
                url = "https://www.googleapis.com/youtube/v3/liveChat/messages",
                headers = mapOf("Authorization" to "Bearer $GOOGLE_TOKEN"),
                queryParams = mapOf(
                    "liveChatId" to GOOGLE_LIVE_CHAT_ID,
                    "part" to "snippet,authorDetails",
                    "pageToken" to ""
                )
            )
        }
    }


    @Test
    fun `given successful response, when isLiveStreamActive called, then should emit true`() = runTest {
        // Given
        val liveBroadcastsResponse = LiveBroadcastsResponse(
            items = listOf(
                LiveBroadcastItem(
                    id = "id",
                    status = LiveBroadcastStatus(
                        lifeCycleStatus = "lifeCycleStatus",
                        privacyStatus = "privacyStatus",
                        recordingStatus = "recordingStatus"
                    )
                )
            ),
            kind = "kind",
            etag = "etag"
        )

        coEvery {
            apiService.request(
                url = "https://www.googleapis.com/youtube/v3/liveBroadcasts",
                method = HttpMethod.Get,
                headers = mapOf("Authorization" to "Bearer $GOOGLE_TOKEN"),
                queryParams = mapOf(
                    "liveChatId" to GOOGLE_LIVE_CHAT_ID,
                    "part" to "status",
                    "broadcastStatus" to "active",
                    "broadcastType" to "all",
                )
            )
        } returns httpResponse

        every { httpResponse.status } returns HttpStatusCode.OK
        coEvery { httpResponse.body<LiveBroadcastsResponse>() } returns liveBroadcastsResponse

        // When
        val flow = chatYoutubeDataSource.isLiveStreamActive()

        // Then
        val isActive = flow.toList().last()
        isActive shouldBe true

        coVerify {
            apiService.request(
                url = "https://www.googleapis.com/youtube/v3/liveBroadcasts",
                method = HttpMethod.Get,
                headers = mapOf("Authorization" to "Bearer $GOOGLE_TOKEN"),
                queryParams = mapOf(
                    "liveChatId" to GOOGLE_LIVE_CHAT_ID,
                    "part" to "status",
                    "broadcastStatus" to "active",
                    "broadcastType" to "all",
                )
            )
        }
    }

    @Test
    fun `given no active broadcast, when isLiveStreamActive called, then should emit false`() = runTest {
        // Given
        val liveBroadcastsResponse = LiveBroadcastsResponse(
            items = listOf(),
            kind = "kind",
            etag = "etag"
        )
        coEvery {
            apiService.request(
                url = "https://www.googleapis.com/youtube/v3/liveBroadcasts",
                method = HttpMethod.Get,
                headers = mapOf("Authorization" to "Bearer $GOOGLE_TOKEN"),
                queryParams = mapOf(
                    "liveChatId" to GOOGLE_LIVE_CHAT_ID,
                    "part" to "status",
                    "broadcastStatus" to "active",
                    "broadcastType" to "all",
                )
            )
        } returns httpResponse

        every { httpResponse.status } returns HttpStatusCode.OK
        coEvery { httpResponse.body<LiveBroadcastsResponse>() } returns liveBroadcastsResponse

        mockkStatic("kotlinx.coroutines.DelayKt")
        coEvery { delay(1) } just Runs

        // When
        val flow = chatYoutubeDataSource.isLiveStreamActive().take(1)

        // Then
        val isActive = flow.toList().last()
        isActive shouldBe false

        coVerify {
            apiService.request(
                url = "https://www.googleapis.com/youtube/v3/liveBroadcasts",
                method = HttpMethod.Get,
                headers = mapOf("Authorization" to "Bearer $GOOGLE_TOKEN"),
                queryParams = mapOf(
                    "liveChatId" to GOOGLE_LIVE_CHAT_ID,
                    "part" to "status",
                    "broadcastStatus" to "active",
                    "broadcastType" to "all",
                )
            )
        }
    }

    @Test
    fun `given error response, when isLiveStreamActive called, then should log error and emit false`() = runTest {
        // Given
        coEvery {
            apiService.request(
                url = "https://www.googleapis.com/youtube/v3/liveBroadcasts",
                method = HttpMethod.Get,
                headers = mapOf("Authorization" to "Bearer $GOOGLE_TOKEN"),
                queryParams = mapOf(
                    "liveChatId" to GOOGLE_LIVE_CHAT_ID,
                    "part" to "status",
                    "broadcastStatus" to "active",
                    "broadcastType" to "all",
                )
            )
        } returns httpResponse

        every { httpResponse.status } returns HttpStatusCode.BadRequest


        // When
        val flow = chatYoutubeDataSource.isLiveStreamActive()

        // Then
        val isActive = flow.toList().last()
        isActive shouldBe false

        coVerify {
            apiService.request(
                url = "https://www.googleapis.com/youtube/v3/liveBroadcasts",
                method = HttpMethod.Get,
                headers = mapOf("Authorization" to "Bearer $GOOGLE_TOKEN"),
                queryParams = mapOf(
                    "liveChatId" to GOOGLE_LIVE_CHAT_ID,
                    "part" to "status",
                    "broadcastStatus" to "active",
                    "broadcastType" to "all",
                )
            )
        }
    }
}