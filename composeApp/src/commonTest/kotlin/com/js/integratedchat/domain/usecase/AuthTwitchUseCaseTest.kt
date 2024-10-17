package com.js.integratedchat.domain.usecase

import Constants.TWITCH_REDIRECT_URI
import Constants.TWITCH_SCOPES
import com.js.integratedchat.BuildConfig
import io.kotest.matchers.shouldBe
import io.mockk.MockKAnnotations
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import java.net.URLEncoder
import kotlin.test.Test

class AuthTwitchUseCaseTest {

    private lateinit var authTwitchUseCase: AuthTwitchUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        authTwitchUseCase = AuthTwitchUseCaseImpl()
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `signIn should return correct intent`() = runTest {

        val scopes: String = URLEncoder.encode(listOf(TWITCH_SCOPES).joinToString(" "), "UTF-8")

        val uri = "https://id.twitch.tv/oauth2/authorize" +
                "?client_id=${BuildConfig.TWITCH_CLIENT_ID}" +
                "&redirect_uri=$TWITCH_REDIRECT_URI" +
                "&response_type=code" +
                "&scope=$scopes"

        // When
        val result = authTwitchUseCase.signIn() as String

        // Then
        result shouldBe  uri
    }
}