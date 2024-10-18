package com.js.integratedchat.data.repository

import com.js.integratedchat.data.datasource.TokenDataSource
import com.js.integratedchat.data.entity.TokenResponseRemoteEntity
import com.js.integratedchat.domain.entity.TokenEntity
import io.kotest.matchers.shouldBe
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class TokenRepositoryImplTest {

    @MockK
    private lateinit var tokenDataSource: TokenDataSource

    private lateinit var tokenRepositoryImpl: TokenRepositoryImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        tokenRepositoryImpl = TokenRepositoryImpl(
            tokenDataSource = tokenDataSource
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `Given token details, when fetchToken is called, then it should return TokenEntity flow`() = runTest {
        // Given
        val tokenUrl = "https://example.com/token"
        val clientId = "clientId123"
        val clientSecret = "clientSecret123"
        val authorizationCode = "authCode123"
        val redirectUri = "https://example.com/redirect"
        val tokenEntity = TokenEntity(
            refreshToken = "refreshToken",
            accessToken = "accessToken",
            expiresIn = 3600,
            tokenType = "bearer"
        )
        val tokenResponseRemoteEntity = TokenResponseRemoteEntity(
            refreshToken = "refreshToken",
            accessToken = "accessToken",
            expiresIn = 3600,
            tokenType = "bearer"
        )

        coEvery {
            tokenDataSource.fetchToken(
                tokenUrl = tokenUrl,
                clientId =  clientId,
                clientSecret = clientSecret,
                authorizationCode = authorizationCode,
                redirectUri = redirectUri
            )
        } returns flowOf(tokenResponseRemoteEntity)

        // When
        val result = tokenRepositoryImpl.fetchToken(
            tokenUrl = tokenUrl,
            clientId =  clientId,
            clientSecret = clientSecret,
            authorizationCode = authorizationCode,
            redirectUri = redirectUri
        ).toList()

        // Then
        result.size shouldBe 1
        result[0] shouldBe tokenEntity

        coVerify {
            tokenDataSource.fetchToken(
                tokenUrl = tokenUrl,
                clientId = clientId,
                clientSecret = clientSecret,
                authorizationCode = authorizationCode,
                redirectUri = redirectUri
            )
        }
        confirmVerified(tokenDataSource)
    }
}