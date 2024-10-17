package com.js.integratedchat.domain.usecase

import com.js.integratedchat.data.repository.TokenRepository
import com.js.integratedchat.data.repository.UserRepository
import com.js.integratedchat.domain.entity.TokenEntity
import com.js.integratedchat.domain.entity.UserEntity
import io.kotest.matchers.shouldBe
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class AuthTwitchUseCaseTest {

    @MockK
    private lateinit var userRepository: UserRepository
    @MockK
    private lateinit var tokenRepository: TokenRepository

    private lateinit var authTwitchUseCase: AuthTwitchUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        authTwitchUseCase = AuthTwitchUseCase(userRepository, tokenRepository)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getUser should return user when token and user fetch are successful`() = runTest {
        // Given
        val authorizationCode = "validCode"
        val token = TokenEntity(
            accessToken = "accessToken",
            refreshToken = "refreshToken",
            tokenType = "tokenType",
            expiresIn = 10
        )
        val user = mockk<UserEntity>(relaxed = true)

        coEvery {
            tokenRepository.fetchToken(any(), any(), any(), any(), any())
        } returns flow { emit(token) }

        coEvery {
            userRepository.fetchUserTwitch(any(), any(), any())
        } returns flow { emit(user) }

        // When
        val result = authTwitchUseCase.getUser(authorizationCode).last()

        // Then
        result shouldBe user
        coVerify { tokenRepository.fetchToken(any(), any(), any(), any(), any()) }
        coVerify { userRepository.fetchUserTwitch(any(), any(), any()) }
    }

    @Test
    fun `getUser should return null when token fetch fails`() = runTest {
        // Given
        val authorizationCode = "invalidCode"

        coEvery {
            tokenRepository.fetchToken(
                any(), any(), any(), any(), any()
            )
        } throws Exception("Token fetch failed")

        // When
        val result = authTwitchUseCase.getUser(authorizationCode).toList()

        // Then
        result.size shouldBe 0
        coVerify { tokenRepository.fetchToken(any(), any(), any(), any(), any()) }
    }
}
