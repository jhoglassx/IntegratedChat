package com.js.integratedchat.data.datasource

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import com.js.integratedchat.provider.DispatcherProvider
import com.js.integratedchat.service.ApiService
import io.kotest.matchers.shouldBe
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.mockk.coEvery
import io.mockk.coVerify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

import org.junit.After
import org.junit.Before
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TokenDataSourceImplTest {


    @MockK
    private lateinit var apiService: ApiService

    private lateinit var dispatcherProvider: DispatcherProvider

    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testScheduler)

    private lateinit var tokenDataSource: TokenDataSourceImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        dispatcherProvider = mockk(relaxed = true)
        every { dispatcherProvider.IO } returns testDispatcher

        Dispatchers.setMain(testDispatcher)

        tokenDataSource = TokenDataSourceImpl(dispatcherProvider, apiService)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }


    @Test
    fun `fetchToken should return token response when API call is successful`() = runTest {
        // Given
        val tokenUrl = "https://example.com/token"
        val clientId = "clientId"
        val clientSecret = "clientSecret"
        val authorizationCode = "authCode"
        val redirectUri = "https://example.com/redirect"
        val responseJson = """
            {
                "access_token": "accessToken",
                "refresh_token": "refreshToken",
                "expires_in": 3600,
                "token_type": "Bearer"
            }
        """
        val httpResponse = mockk<HttpResponse> {
            coEvery { status } returns HttpStatusCode.OK
            coEvery { body<String>() } returns responseJson
        }

        coEvery {
            apiService.request(any(), any(), any(), any(), any())
        } returns httpResponse

        // When
        val result = tokenDataSource.fetchToken(tokenUrl, clientId, clientSecret, authorizationCode, redirectUri).toList()

        // Then
        result.size shouldBe 1
        val tokenResponse = result[0]
        tokenResponse.accessToken shouldBe "accessToken"
        tokenResponse.refreshToken shouldBe "refreshToken"
        tokenResponse.expiresIn shouldBe 3600
        tokenResponse.tokenType shouldBe "Bearer"

        coVerify {
            apiService.request(any(), any(), any(), any(), any())
        }
    }

    @Test(expected = Exception::class)
    fun `fetchToken should throw exception when API call fails`() = runTest {
        // Given
        val tokenUrl = "https://example.com/token"
        val clientId = "clientId"
        val clientSecret = "clientSecret"
        val authorizationCode = "authCode"
        val redirectUri = "https://example.com/redirect"
        val httpResponse = mockk<HttpResponse> {
            coEvery { status } returns HttpStatusCode.BadRequest
            coEvery { body<String>() } returns "Error response"
        }

        coEvery {
            apiService.request(any(), any(), any(), any(), any())
        } returns httpResponse

        // When
        tokenDataSource.fetchToken(tokenUrl, clientId, clientSecret, authorizationCode, redirectUri).first()

        // Then
        // Exception is expected
    }
}