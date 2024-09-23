package com.js.project.data.datasource

import Constants.GOOGLE_LIVE_CHAT_ID
import Constants.GOOGLE_TOKEN
import com.js.project.data.entity.UserResponseRemoteEntity
import com.js.project.provider.DispatcherProvider
import com.js.project.service.ApiService
import io.kotest.matchers.shouldBe
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.unmockkAll
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
    fun `getYouTubeChat should return chat messages when API call is successful`() = runTest {
        // Given
        val googleUser = UserResponseRemoteEntity(
            id = "userId",
            name = "Author",
            email = "",
            displayName = "Author",
            imageUrl = ""
        )
        val responseJson = """{
            "items": [
                {
                    "id": "messageId",
                    "snippet": {
                        "publishedAt": "2021-09-01T00:00:00Z",
                        "displayMessage": "Hello, world!"
                    },
                    "authorDetails": {
                        "displayName": "Author",
                        "channelId": "userId",
                        "isChatOwner": true,
                        "isChatModerator": false,
                        "isChatSponsor": false
                    }
                }
            ],
            "nextPageToken": ""
        }"""

        val httpResponse: HttpResponse = mockk {
            coEvery { status } returns HttpStatusCode.OK
            coEvery { body<String>() } returns responseJson
        }

        coEvery {
            apiService.request(
                url = "https://www.googleapis.com/youtube/v3/liveChat/messages",
                method = HttpMethod.Get,
                headers = mapOf("Authorization" to "Bearer $GOOGLE_TOKEN"),
                queryParams = mapOf(
                    "liveChatId" to GOOGLE_LIVE_CHAT_ID.toString(),
                    "part" to "snippet,authorDetails",
                    "pageToken" to ""
                )
            )
        } returns httpResponse

        // When
        val result = chatYoutubeDataSource.getYouTubeChat(googleUser).first()

        // Then

        val chatMessage = result
        chatMessage.id shouldBe "messageId"
        chatMessage.displayName shouldBe "Author"
        chatMessage.message shouldBe "Hello, world!"

        coVerify(exactly = 1) {
            apiService.request(any(), any(), any(), any(), any())
        }
    }

    @Test
    fun `getYouTubeChat should handle exceptions gracefully`() = runTest {
        // Given
        val googleUser = UserResponseRemoteEntity(
            id = "userId",
            name = "Author",
            email = "",
            displayName = "Author",
            imageUrl = ""
        )
        coEvery {
            apiService.request(any(), any(), any(), any(), any())
        } throws Exception("Network error")

        // When
        val result = chatYoutubeDataSource.getYouTubeChat(googleUser).toList()

        // Then
        result.size shouldBe 0

        coVerify(exactly = 1) {
            apiService.request(any(), any(), any(), any(), any())
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