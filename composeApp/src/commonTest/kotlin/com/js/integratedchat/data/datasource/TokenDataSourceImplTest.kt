package com.js.integratedchat.data.datasource

import com.js.integratedchat.data.entity.TokenResponseRemoteEntity
import com.js.integratedchat.provider.DispatcherProvider
import com.js.integratedchat.service.ApiService
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
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertFailsWith

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
    fun `fetchToken should emit token when response is OK`() = runTest {
        // Given
        val tokenUrl = "https://example.com/token"
        val clientId = "dummyClientId"
        val clientSecret = "dummyClientSecret"
        val authorizationCode = "dummyAuthorizationCode"
        val redirectUri = "https://example.com/redirect"
        val tokenResponse = TokenResponseRemoteEntity(
            accessToken = "dummyAccessToken",
            refreshToken = "dummyRefreshToken",
            expiresIn = 3600,
            tokenType = "bearer"
        )
        val httpResponse = mockk<HttpResponse>(relaxed = true)

        coEvery { apiService.request(any(), any(), any(), any()) } returns httpResponse
        every { httpResponse.status } returns HttpStatusCode.OK
        coEvery { httpResponse.body<TokenResponseRemoteEntity>() } returns tokenResponse

        // When
        val flow = tokenDataSource.fetchToken(
            tokenUrl, clientId, clientSecret, authorizationCode, redirectUri
        )

        // Then
        flow.collect { emittedToken ->
            emittedToken shouldBe tokenResponse
        }

        coVerify {
            apiService.request(
                url = tokenUrl,
                method = HttpMethod.Post,
                headers = mapOf("Content-Type" to "application/x-www-form-urlencoded"),
                body = mapOf(
                    "client_id" to clientId,
                    "client_secret" to clientSecret,
                    "code" to authorizationCode,
                    "grant_type" to "authorization_code",
                    "redirect_uri" to redirectUri
                )
            )
        }
    }

    @Test
    fun `fetchToken should throw exception when response is not OK`() = runTest {
        // Given
        val tokenUrl = "https://example.com/token"
        val clientId = "dummyClientId"
        val clientSecret = "dummyClientSecret"
        val authorizationCode = "dummyAuthorizationCode"
        val redirectUri = "https://example.com/redirect"
        val httpResponse = mockk<HttpResponse>(relaxed = true)

        coEvery { apiService.request(any(), any(), any(), any()) } returns httpResponse
        every { httpResponse.status } returns HttpStatusCode.BadRequest

        // Then
        assertFailsWith<Exception> {
            tokenDataSource.fetchToken(tokenUrl, clientId, clientSecret, authorizationCode, redirectUri).first()
        }
    }
}