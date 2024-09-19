package com.js.project.data.datasource


import com.js.project.service.ApiService
import io.kotest.matchers.shouldBe
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertThrows
import org.junit.Before
import kotlin.test.Test

class ChatYouTubeLiveIdDataSourceImplTest {

    @MockK
    private lateinit var apiService: ApiService

    private lateinit var chatYouTubeLiveIdDataSource: ChatYouTubeLiveIdDataSourceImpl


    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        chatYouTubeLiveIdDataSource = ChatYouTubeLiveIdDataSourceImpl(apiService)

    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getYouTubeLiveChatId when result OK return liveChatId`() = runTest {
        //when
        val httpResponse = mockk<HttpResponse>(relaxed = true)
        val responseBody = """
            {
                "items": [
                    {
                        "snippet": {
                            "liveChatId": "liveChatId"
                        }
                    }
                ]
            }
        """.trimIndent()

        coEvery { httpResponse.status } returns HttpStatusCode.OK
        coEvery { httpResponse.body<String>() } returns responseBody

        coEvery {
            apiService.request(
                url = any(),
                method = any(),
                headers = any(),
                queryParams = any()
            )
        } returns httpResponse

        val result = chatYouTubeLiveIdDataSource.getYouTubeLiveChatId("channelId")

        result.collect{
            it  shouldBe "liveChatId"
        }
    }

    @Test
    fun `getYouTubeLiveChatId when result BAD not return liveChatId`() = runTest {
        //when
        val httpResponse = mockk<HttpResponse>(relaxed = true)
        val responseBody = """
            {
                "items": [
                    {
                        "snippet": {
                            "liveChatId": ""
                        }
                    }
                ]
            }
        """.trimIndent()

        coEvery { httpResponse.status } returns HttpStatusCode.BadRequest
        coEvery { httpResponse.body<String>() } returns responseBody

        coEvery {
            apiService.request(
                url = any(),
                method = any(),
                headers = any(),
                queryParams = any()
            )
        } returns httpResponse

        val exception = assertThrows(Exception::class.java) {
            runBlocking {
                chatYouTubeLiveIdDataSource.getYouTubeLiveChatId("channelId").first()
            }
        }
        exception.message shouldBe "Failed to fetch live chat ID: 400 Bad Request"
    }
}