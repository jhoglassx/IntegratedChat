package com.js.integratedchat.data.datasource

import com.js.integratedchat.data.entity.UserGoogleRemoteEntity
import com.js.integratedchat.data.entity.UserResponse
import com.js.integratedchat.data.entity.UserTwitchRemoteEntity
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
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UserDataSourceImplTest {

    @MockK
    private lateinit var apiService: ApiService

    private lateinit var dispatcherProvider: DispatcherProvider

    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testScheduler)

    private lateinit var userDataSource: UserDataSourceImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        dispatcherProvider = mockk(relaxed = true)
        every { dispatcherProvider.IO } returns testDispatcher

        Dispatchers.setMain(testDispatcher)

        userDataSource = UserDataSourceImpl(apiService, dispatcherProvider)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `fetchUserGoogle should emit user when response is OK`() = runTest {
        // Given
        val url = "https://example.com/userinfo"
        val accessToken = "dummyAccessToken"
        val clientId = "dummyClientId"
        val method = HttpMethod.Get
        val headers = mapOf("Authorization" to "Bearer $accessToken", "Client-Id" to clientId)
        val queryParams = mapOf("alt" to "json")

        val userEntity = UserGoogleRemoteEntity(
            id = "dummyId",
            email = "dummyEmail",
            name = "dummyName",
            displayName = "dummyDisplayName",
            imageUrl = "dummyImageUrl"
        )
        val httpResponse = mockk<HttpResponse>(relaxed = true)

        coEvery {
            apiService.request(
                url = url,
                method = method,
                headers = headers,
                queryParams = queryParams
            )
        } returns httpResponse
        every { httpResponse.status } returns HttpStatusCode.OK
        coEvery { httpResponse.body<UserGoogleRemoteEntity>() } returns userEntity

        // When
        val flow = userDataSource.fetchUserGoogle(url, accessToken, clientId)

        // Then
        flow.collect { emittedUser ->
            emittedUser shouldBe userEntity
        }

        coVerify {
            apiService.request(
                url = url,
                method = HttpMethod.Get,
                headers = mapOf("Authorization" to "Bearer $accessToken", "Client-Id" to clientId),
                queryParams = mapOf("alt" to "json")
            )
        }
    }

    @Test
    fun `fetchUserTwitch should emit user when response is OK`() = runTest {
        // Given
        val userInfoUrl = "https://example.com/userinfo"
        val accessToken = "dummyAccessToken"
        val clientId = "dummyClientId"
        val userResponse = UserResponse(data = listOf(UserTwitchRemoteEntity(
            id = "dummyId",
            email = "dummyEmail",
            login = "dummyName",
            displayName = "dummyDisplayName",
            profileImageUrl = "dummyImageUrl",
            offlineImageUrl = "dummyOfflineImageUrl",
            viewCount = 0,
            broadcasterType = "dummyBroadcasterType",
            description = "dummyDescription",
            type = "dummyType",
            createdAt = "dummyCreatedAt"
        )))
        val httpResponse = mockk<HttpResponse>(relaxed = true)

        coEvery { apiService.request(any(), any(), any(), any()) } returns httpResponse
        every { httpResponse.status } returns HttpStatusCode.OK
        coEvery { httpResponse.body<UserResponse>() } returns userResponse

        // When
        val flow = userDataSource.fetchUserTwitch(userInfoUrl, accessToken, clientId)

        // Then
        flow.collect { emittedUser ->
            emittedUser shouldBe userResponse.data.first()
        }

        coVerify {
            apiService.request(
                url = userInfoUrl,
                method = HttpMethod.Get,
                headers = mapOf("Authorization" to "Bearer $accessToken", "Client-Id" to clientId)
            )
        }
    }

    @Test
    fun `fetchUserGoogle should not emit user when response is not OK`() = runTest {
        // Given
        val url = "https://example.com/userinfo"
        val accessToken = "dummyAccessToken"
        val clientId = "dummyClientId"
        val httpResponse = mockk<HttpResponse>(relaxed = true)

        coEvery {
            apiService.request(
                url = url,
                method = HttpMethod.Get,
                headers = mapOf("Authorization" to "Bearer $accessToken", "Client-Id" to clientId),
                queryParams = mapOf("alt" to "json")
            )
        } returns httpResponse
        every { httpResponse.status } returns HttpStatusCode.BadRequest

        // When
        val flow = userDataSource.fetchUserGoogle(url, accessToken, clientId)

        // Then
        flow.collect {
            assert(false) { "Flow should not emit any value" }
        }
    }

    @Test
    fun `fetchUserTwitch should not emit user when response is not OK`() = runTest {
        // Given
        val userInfoUrl = "https://example.com/userinfo"
        val accessToken = "dummyAccessToken"
        val clientId = "dummyClientId"
        val httpResponse = mockk<HttpResponse>(relaxed = true)

        coEvery { apiService.request(any(), any(), any(), any()) } returns httpResponse
        every { httpResponse.status } returns HttpStatusCode.BadRequest

        // When
        val flow = userDataSource.fetchUserTwitch(userInfoUrl, accessToken, clientId)

        // Then
        flow.collect {
            assert(false) { "Flow should not emit any value" }
        }
    }
}