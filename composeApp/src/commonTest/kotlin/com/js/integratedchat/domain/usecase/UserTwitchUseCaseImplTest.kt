package com.js.integratedchat.domain.usecase

import com.js.integratedchat.data.repository.TokenRepository
import com.js.integratedchat.data.repository.UserRepository
import com.js.integratedchat.domain.entity.TokenEntity
import com.js.integratedchat.domain.entity.UserEntity
import io.kotest.matchers.shouldBe
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import kotlin.test.Test

class UserTwitchUseCaseImplTest {


    @MockK
    private lateinit var tokenRepository: TokenRepository
    @MockK
    private lateinit var userRepository: UserRepository

    private lateinit var userTwitchUseCase: UserTwitchUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        userTwitchUseCase = UserTwitchUseCaseImpl(
            userRepository = userRepository,
            tokenRepository = tokenRepository
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getUser should return user when fetchToken and fetchUserTwitch are successful`() = runTest {
        // Given
        val authorizationCode = "valid_authorization_code"
        val token = TokenEntity(
            refreshToken = "refreshToken",
            accessToken = "accessToken",
            tokenType = "tokenType",
            expiresIn = 10
        )
        val user = UserEntity(
            id = "id",
            name = "name",
            displayName = "displayName",
            email = "email",
            imageUrl = "imageUrl"
        )

        coEvery {
            tokenRepository.fetchToken(any(), any(), any(), any(), any())
        } returns flowOf(token)
        coEvery {
            userRepository.fetchUserTwitch(any(), any(), any())
        } returns flowOf(user)

        // When
        val result = userTwitchUseCase.getUser(authorizationCode).last()

        // Then
        result shouldBe user
    }

    @Test
    fun `getUser should handle ApiException`() = runTest {
        // Given
        val authorizationCode = "invalid_authorization_code"

        coEvery {
            tokenRepository.fetchToken(any(), any(), any(), any(), any())
        } throws Exception("Status.RESULT_CANCELED")

        // When
        val result = userTwitchUseCase.getUser(authorizationCode).lastOrNull()

        // Then
        result shouldBe null
    }
}