package com.js.integratedchat.domain.usecase

import Constants.GOOGLE_DESKTOP_REDIRECT_URI
import Constants.GOOGLE_SCOPES
import com.js.integratedchat.data.repository.TokenRepository
import com.js.integratedchat.data.repository.UserRepository
import com.js.integratedchat.domain.entity.TokenEntity
import com.js.integratedchat.domain.entity.UserEntity
import com.js.integratedchat.provider.KeysConfig
import io.kotest.matchers.shouldBe
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import java.net.URLEncoder
import kotlin.test.Test

@ExperimentalCoroutinesApi
class AuthGoogleUseCaseTest {

    @MockK
    private lateinit var tokenRepository: TokenRepository
    @MockK
    private lateinit var userRepository: UserRepository

    private lateinit var authGoogleUseCase: AuthGoogleUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        authGoogleUseCase = AuthGoogleUseCase(tokenRepository, userRepository)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `signIn should return valid auth URL`() = runTest {
        // Given
        val expectedUrl = "https://accounts.google.com/o/oauth2/auth" +
                "?client_id=${URLEncoder.encode(KeysConfig.googleDesktopClientId, "UTF-8")}" +
                "&redirect_uri=${URLEncoder.encode(GOOGLE_DESKTOP_REDIRECT_URI, "UTF-8")}" +
                "&response_type=code" +
                "&scope=${URLEncoder.encode(GOOGLE_SCOPES, "UTF-8")}"

        // When
        val result = authGoogleUseCase.signIn()

        // Then
        result shouldBe expectedUrl
    }

    @Test
    fun `getUser should return user when token and user fetch are successful`() = runTest {
        // Given
        val authorizationCode = "auth_code"
        val token = TokenEntity(
            tokenType = "tokenType",
            refreshToken = "refreshToken",
            accessToken = "accessToken",
            expiresIn = 10
        )
        val user = UserEntity(
            id = "id",
            name = "name",
            email = "email",
            displayName = "displayName",
            imageUrl = "imageUrl"
        )

        coEvery {
            tokenRepository.fetchToken(any(), any(), any(), any(), any())
        } returns flow { emit(token) }
        coEvery {
            userRepository.fetchUserGoogle(any(), any(), any())
        } returns flow { emit(user) }

        // When
        val result = authGoogleUseCase.getUser(authorizationCode).last()

        // Then
        result shouldBe user
        coVerify { tokenRepository.fetchToken(any(), any(), any(), any(), any()) }
        coVerify { userRepository.fetchUserGoogle(any(), any(), any()) }
    }

    @Test
    fun `getUser should return empty flow when exception occurs`() = runTest {
        // Given
        val authorizationCode = "auth_code"

        coEvery {
            tokenRepository.fetchToken(any(), any(), any(), any(), any())
        } throws Exception()
        coEvery {
            userRepository.fetchUserGoogle(any(), any(), any())
        } throws Exception()

        // When
        val result = authGoogleUseCase.getUser(authorizationCode).toList()

        // Then
        result.size shouldBe 0
        coVerify { tokenRepository.fetchToken(any(), any(), any(), any(), any()) }
        coVerify (exactly = 0) { userRepository.fetchUserGoogle(any(), any(), any()) }
    }
}