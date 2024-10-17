package com.js.integratedchat.data.datasource

import Constants.GOOGLE_LIVE_CHAT_ID
import Constants.GOOGLE_TOKEN
import com.js.integratedchat.data.entity.UserRemoteEntity
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
import io.mockk.unmockkAll
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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
    fun `getYouTubeChat emits messages on success`() = runTest {
        // Arrange
        val googleUser = UserRemoteEntity(
            id = "id",
            email = "email",
            name = "name",
            displayName = "displayName",
            imageUrl = "imageUrl"
        )
        val mockResponse = mockk<HttpResponse>()
        val jsonResponse = """
            {
                "items": [
                    {
                        "id": "message1",
                        "snippet": {
                            "publishedAt": "2024-10-10T10:00:00Z",
                            "displayMessage": "Hello, world!"
                        },
                        "authorDetails": {
                            "displayName": "Author1",
                            "channelId": "channel1"
                        }
                    }
                ],
                "nextPageToken": "nextPageToken"
            }
        """

        every { mockResponse.status } returns HttpStatusCode.OK
        //coEvery { mockResponse.bodyAsText() } returns jsonResponse
        coEvery {
            apiService.request(
                url = any(),
                method = HttpMethod.Get,
                headers = any(),
                queryParams = any()
            )
        } returns mockResponse

        // Act
        val messages = chatYoutubeDataSource.getYouTubeChat(googleUser).toList()

        // Assert
        assertEquals(1, messages.size)
        val message = messages[0]
        message.id shouldBe "message1"
        message.displayName shouldBe "Author1"
        message.message shouldBe "Hello, world!"
        message.userId shouldBe "channel1"
        message.source shouldBe SourceEnum.YOUTUBE

        coVerify(exactly = 1) {
            apiService.request(
                url = "https://www.googleapis.com/youtube/v3/liveChat/messages",
                method = HttpMethod.Get,
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
    fun `getYouTubeChat logs error on failure`() = runTest {
        // Arrange
        val googleUser = UserRemoteEntity(
            id = "id",
            email = "email",
            name = "name",
            displayName = "displayName",
            imageUrl = "imageUrl"
        )
        val mockResponse = mockk<HttpResponse>()

        every { mockResponse.status } returns HttpStatusCode.BadRequest
        coEvery { mockResponse.bodyAsText() } returns "Error"

        coEvery {
            apiService.request(
                url = any(),
                method = HttpMethod.Get,
                headers = any(),
                queryParams = any()
            )
        } returns mockResponse

        // Act
        chatYoutubeDataSource.getYouTubeChat(googleUser).first()

        // Assert
        coVerify(exactly = 1) {
            apiService.request(
                url = "https://www.googleapis.com/youtube/v3/liveChat/messages",
                method = HttpMethod.Get,
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
    fun `isLiveStreamActive should emit true when response is OK and items are not empty`() = runTest {
        // Given
        val responseJson = """
            {
                "items": [
                    {
                        "id": "id"
                    }
                ]
            }
        """
        val httpResponse: HttpResponse = mockk {
            coEvery { status } returns HttpStatusCode.OK
            coEvery { body<String>() } returns responseJson
        }
        coEvery {
            apiService.request(any(), any(), any(), any(), any())
        } returns httpResponse

        // When
        val result = chatYoutubeDataSource.isLiveStreamActive().first()

        // Then
        result shouldBe true
        coVerify(exactly = 1) { apiService.request(any(), any(), any(), any(), any()) }
    }

    @Test
    fun `isLiveStreamActive should emit false when response is OK and items are empty`() = runTest {
        // Given
        val responseJson = """
            {
                "items": []
            }
        """
        val httpResponse: HttpResponse = mockk {
            coEvery { status } returns HttpStatusCode.OK
            coEvery { body<String>() } returns responseJson
        }
        coEvery { apiService.request(any(), any(), any(), any(), any()) } returns httpResponse

        // When
        val result = chatYoutubeDataSource.isLiveStreamActive().first()

        // Then
        result shouldBe false
        coVerify(exactly = 1) {
            apiService.request(any(), any(), any(), any(), any())
        }
    }

    @Test
    fun `isLiveStreamActive should emit false when response is not OK`() = runTest {
        // Given
        val httpResponse: HttpResponse = mockk {
            coEvery { status } returns HttpStatusCode.BadRequest
        }
        coEvery { apiService.request(any(), any(), any(), any()) } returns httpResponse

        // When
        val result = chatYoutubeDataSource.isLiveStreamActive().first()

        // Then
        result shouldBe false
        coVerify(exactly = 1) {
            apiService.request(any(), any(), any(), any(), any())
        }
    }

    @Test
    fun `isLiveStreamActive should emit false when an exception is thrown`() = runTest {
        // Given
        coEvery {
            apiService.request(any(), any(), any(), any(), any())
        } throws Exception("Network error")

        // When
        val result = chatYoutubeDataSource.isLiveStreamActive().first()

        // Then
        result shouldBe false
        coVerify(exactly = 1) {
            apiService.request(any(), any(), any(), any(), any())
        }
    }
}