package com.js.integratedchat.domain.usecase

import Constants.GOOGLE_REDIRECT_URI
import Constants.GOOGLE_SCOPES
import com.js.integratedchat.data.Keys
import io.kotest.matchers.shouldBe
import io.mockk.MockKAnnotations
import io.mockk.unmockkAll
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import java.net.URLEncoder
import kotlin.test.Test

@ExperimentalCoroutinesApi
class AuthGoogleUseCaseTest {

    private lateinit var authGoogleUseCase: AuthGoogleUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        authGoogleUseCase = AuthGoogleUseCase(

        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `signIn should return valid auth URL`() = runTest {
        // Given
        val expectedUrl = "https://accounts.google.com/o/oauth2/auth" +
                "?client_id=${URLEncoder.encode(Keys.googleClientId, "UTF-8")}" +
                "&redirect_uri=${URLEncoder.encode(GOOGLE_REDIRECT_URI, "UTF-8")}" +
                "&response_type=code" +
                "&scope=${URLEncoder.encode(GOOGLE_SCOPES, "UTF-8")}"

        // When
        val result = authGoogleUseCase.signIn()

        // Then
        result shouldBe expectedUrl
    }
}