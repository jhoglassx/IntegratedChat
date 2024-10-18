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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import kotlin.test.Test

class UserGoogleUseCaseImplTest {
    @MockK
    private lateinit var tokenRepository: TokenRepository
    @MockK
    private lateinit var userRepository: UserRepository

    private lateinit var userGoogleUseCase: UserGoogleUseCase


    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        userGoogleUseCase = UserGoogleUseCaseImpl(
            userRepository = userRepository,
            tokenRepository = tokenRepository
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `getUser should return user when valid authorizationCode is provided`() = runTest {
        // Given
        val authorizationCode = "validAuthorizationCode"
        val tokenResponse = TokenEntity(
            refreshToken = "refreshToken",
            accessToken = "accessToken",
            tokenType = "tokenType",
            expiresIn = 10
        )
        val userEntity = UserEntity(
            id = "id",
            name = "name",
            displayName = "displayName",
            email = "email",
            imageUrl = "imageUrl"
        )
        coEvery {
            tokenRepository.fetchToken(any(), any(), any(), any(), any())
        } returns flow { emit(tokenResponse) }
        coEvery {
            userRepository.fetchUserGoogle(any(), any(), any())
        } returns flow { emit(userEntity) }

        // When
        val resultFlow: Flow<UserEntity> = userGoogleUseCase.getUser(authorizationCode)
        val result = resultFlow.last()

        // Then
        result shouldBe userEntity
    }

    @Test
    fun `getUser should return empty flow when ApiException is thrown`() = runTest {
        // Given
        val authorizationCode = "invalidAuthorizationCode"
        coEvery {
            tokenRepository.fetchToken(any(), any(), any(), any(), any())
        } throws Exception("Status.RESULT_CANCELED")

        // When
        val resultFlow: Flow<UserEntity> = userGoogleUseCase.getUser(authorizationCode)
        val result = resultFlow.toList()

        // Then
        result.size shouldBe 0
    }
}