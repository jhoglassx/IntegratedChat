package com.js.integratedchat.data.datasource


import Constants.GOOGLE_TOKEN
import co.touchlab.kermit.Logger
import com.js.integratedchat.data.entity.LiveChatIdResponse
import com.js.integratedchat.data.entity.LiveChatItem
import com.js.integratedchat.data.entity.LiveChatSnippet
import com.js.integratedchat.ext.error
import com.js.integratedchat.provider.DispatcherProvider
import com.js.integratedchat.service.ApiService
import io.kotest.matchers.shouldBe
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.unmockkObject
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.junit.After
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertFailsWith

@OptIn(ExperimentalCoroutinesApi::class)
class ChatYouTubeLiveIdDataSourceImplTest {

    @MockK
    private lateinit var apiService: ApiService
    @MockK
    private lateinit var dispatcherProvider: DispatcherProvider

    private lateinit var chatYouTubeLiveIdDataSource: ChatYouTubeLiveIdDataSourceImpl

    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testScheduler)

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        dispatcherProvider = mockk(relaxed = true)
        every { dispatcherProvider.IO } returns testDispatcher

        Dispatchers.setMain(testDispatcher)

        chatYouTubeLiveIdDataSource = ChatYouTubeLiveIdDataSourceImpl(
            apiService = apiService,
            dispatcherProvider = dispatcherProvider
        )

    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `getYouTubeLiveChatId should emit chat ID when response is OK`() = runTest {
        // Given
        val channelId = "dummyChannelId"
        val expectedLiveChatId = "dummyLiveChatId"
        val response = mockk<HttpResponse>(relaxed = true)
        val liveChatIdResponse = LiveChatIdResponse(
            items = listOf(
                LiveChatItem(snippet = LiveChatSnippet(liveChatId = expectedLiveChatId))
            )
        )

        // Mock API response
        coEvery {
            apiService.request(
                url = "https://www.googleapis.com/youtube/v3/liveBroadcasts",
                method = HttpMethod.Get,
                headers = mapOf("Authorization" to "Bearer $GOOGLE_TOKEN"),
                queryParams = mapOf(
                    "channelId" to channelId,
                    "broadcastType" to "all",
                    "broadcastStatus" to "active",
                    "part" to "snippet"
                )
            )
        } returns response
        every { response.status } returns HttpStatusCode.OK
        coEvery { response.body<LiveChatIdResponse>() } returns liveChatIdResponse

        // When
        val flow = chatYouTubeLiveIdDataSource.getYouTubeLiveChatId(channelId)

        // Then
        flow.collect { emittedChatId ->
            emittedChatId shouldBe expectedLiveChatId
        }

        // Verify API call
        coVerify {
            apiService.request(
                url = "https://www.googleapis.com/youtube/v3/liveBroadcasts",
                method = HttpMethod.Get,
                headers = mapOf("Authorization" to "Bearer $GOOGLE_TOKEN"),
                queryParams = mapOf(
                    "channelId" to channelId,
                    "broadcastType" to "all",
                    "broadcastStatus" to "active",
                    "part" to "snippet"
                )
            )
        }
    }

    @Test
    fun `getYouTubeLiveChatId should throw exception when response is not OK`() = runTest {
        // Given
        val channelId = "dummyChannelId"
        val response = mockk<HttpResponse>(relaxed = true)

        // Mock API response
        coEvery {
            apiService.request(
                url ="https://www.googleapis.com/youtube/v3/liveBroadcasts",
                method = HttpMethod.Get,
                headers = mapOf("Authorization" to "Bearer $GOOGLE_TOKEN"),
                queryParams = mapOf(
                    "channelId" to channelId,
                    "broadcastType" to "all",
                    "broadcastStatus" to "active",
                    "part" to "snippet"
                )
            )
        } returns response
        every { response.status } returns HttpStatusCode.BadRequest

        // When & Then
        val exception = assertFailsWith<Exception> {
            chatYouTubeLiveIdDataSource.getYouTubeLiveChatId(channelId).first()
        }

         exception.message shouldBe "Failed to fetch live chat ID: $response"
    }
}