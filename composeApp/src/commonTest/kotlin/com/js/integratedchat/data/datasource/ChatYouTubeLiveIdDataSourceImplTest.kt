package com.js.integratedchat.data.datasource


import com.js.integratedchat.provider.DispatcherProvider
import com.js.integratedchat.service.ApiService
import io.kotest.matchers.shouldBe
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
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

    @Test(expected = Exception::class)
    fun `getYouTubeLiveChatId throws exception when response is not successful`() = runTest {
        // Given
        val channelId = "testChannelId"
        val response: HttpResponse = mockk {
            coEvery { status } returns HttpStatusCode.BadRequest
        }
        coEvery { apiService.request(any(), any(), any(), any()) } returns response

        // When
        chatYouTubeLiveIdDataSource.getYouTubeLiveChatId(channelId).toList()

        // Then
        // Exception is thrown
        coVerify {
            apiService.request(any(), any(), any(), any())
        }
    }

//    @Test
//    fun `getYouTubeLiveChatId returns empty string when no live chat ID is found`() = runTest {
//        // Given
//        val channelId = "testChannelId"
//        val jsonResponse = buildJsonObject {
//            put("items", buildJsonArray { })
//        }
//        val response: HttpResponse = mockk {
//            coEvery { status } returns HttpStatusCode.OK
//            coEvery { bodyAsText() } returns jsonResponse.toString()
//        }
//        coEvery { apiService.request(any(), any(), any(), any()) } returns response
//
//        // When
//        val result = chatYouTubeLiveIdDataSource.getYouTubeLiveChatId(channelId).toList()
//
//        // Then
//        //result.size shouldBe 1
//        result shouldBe ""
//        coVerify {
//            apiService.request(any(), any(), any(), any())
//        }
//    }
}