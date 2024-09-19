package com.js.project.data.datasource

import com.js.project.provider.DispatcherProvider
import com.js.project.service.ApiService
import io.kotest.matchers.shouldBe
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
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
    fun `fetchUserGoogle should return user response when API call is successful`() = runTest {
        // Given
        val userInfoUrl = "https://example.com/userinfo"
        val accessToken = "accessToken"
        val clientId = "clientId"
        val responseJson = """
            {
                "id": "123",
                "email": "user@example.com",
                "given_name": "John",
                "name": "John Doe",
                "picture": "https://example.com/john.jpg"
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
        val result = userDataSource.fetchUserGoogle(userInfoUrl, accessToken, clientId).toList()

        // Then
        result.size shouldBe 1
        val userResponse = result[0]
        userResponse.id shouldBe "123"
        userResponse.email shouldBe "user@example.com"
        userResponse.name shouldBe "John"
        userResponse.displayName shouldBe "John Doe"
        userResponse.imageUrl shouldBe "https://example.com/john.jpg"

        coVerify {
            apiService.request(any(), any(), any(), any(), any())
        }
    }

    @Test(expected = Exception::class)
    fun `fetchUserGoogle should throw exception when API call fails`() = runTest {
        // Given
        val userInfoUrl = "https://example.com/userinfo"
        val accessToken = "accessToken"
        val clientId = "clientId"
        val httpResponse = mockk<HttpResponse> {
            coEvery { status } returns HttpStatusCode.BadRequest
            coEvery { body<String>() } returns "Error response"
        }

        coEvery {
            apiService.request(any(), any(), any(), any(), any())
        } returns httpResponse

        // When
        userDataSource.fetchUserGoogle(userInfoUrl, accessToken, clientId).first()

        // Then
        // Exception is expected
    }

    @Test
    fun `fetchUserTwitch should return user response when API call is successful`() = runTest {
        // Given
        val userInfoUrl = "https://example.com/userinfo"
        val accessToken = "accessToken"
        val clientId = "clientId"
        val responseJson = """
            {
                "data": [
                    {
                        "id": "123",
                        "email": "user@example.com",
                        "login": "john",
                        "display_name": "John Doe",
                        "profile_image_url": "https://example.com/john.jpg"
                    }
                ]
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
        val result = userDataSource.fetchUserTwitch(userInfoUrl, accessToken, clientId).toList()

        // Then
        result.size shouldBe 1
        val userResponse = result[0]
        userResponse.id shouldBe "123"
        userResponse.email shouldBe "user@example.com"
        userResponse.name shouldBe "john"
        userResponse.displayName shouldBe "John Doe"
        userResponse.imageUrl shouldBe "https://example.com/john.jpg"

        coVerify {
            apiService.request(any(), any(), any(), any(), any())
        }
    }

    @Test(expected = Exception::class)
    fun `fetchUserTwitch should throw exception when API call fails`() = runTest {
        // Given
        val userInfoUrl = "https://example.com/userinfo"
        val accessToken = "accessToken"
        val clientId = "clientId"
        val httpResponse = mockk<HttpResponse> {
            coEvery { status } returns HttpStatusCode.BadRequest
            coEvery { body<String>() } returns "Error response"
        }

        coEvery {
            apiService.request(any(), any(), any(), any(), any())
        } returns httpResponse

        // When
        userDataSource.fetchUserTwitch(userInfoUrl, accessToken, clientId).first()

        // Then
        // Exception is expected
    }
}