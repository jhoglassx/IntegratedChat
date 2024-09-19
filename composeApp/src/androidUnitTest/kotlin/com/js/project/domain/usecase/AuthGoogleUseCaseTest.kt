package com.js.project.domain.usecase

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.text.TextUtils
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Status
import com.js.project.data.repository.TokenRepository
import com.js.project.data.repository.UserRepository
import com.js.project.domain.entity.TokenEntity
import com.js.project.domain.entity.UserEntity
import io.kotest.matchers.shouldBe
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import kotlin.test.Test

class AuthGoogleUseCaseTest {

    @MockK
    private lateinit var context: Context
    @MockK
    private lateinit var tokenRepository: TokenRepository
    @MockK
    private lateinit var userRepository: UserRepository
    @MockK
    private lateinit var packageManager: PackageManager

    private lateinit var authGoogleUseCase: AuthGoogleUseCase


    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        authGoogleUseCase = AuthGoogleUseCase(context, tokenRepository, userRepository)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `signIn should return signInIntent`() = runTest {

        mockkStatic(GoogleSignIn::class)
        mockkStatic(TextUtils::class)
        mockkStatic(Log::class)

        every { context.applicationContext } returns context
        every { context.packageManager } returns packageManager
        every { TextUtils.isEmpty(any()) } returns false
        every { Log.e(any(), any()) } returns 0

        val googleSignInClient: GoogleSignInClient = mockk(relaxed = true)
        val googleSignInOptions: GoogleSignInOptions = mockk(relaxed = true)

        //Given
        val signInIntent = mockk<Intent>(relaxed = true)

        every {
            googleSignInClient.signInIntent
        } returns signInIntent

        every {
            GoogleSignIn.getClient(context, googleSignInOptions)
        } returns googleSignInClient

        // Mock the builder pattern
        mockkConstructor(GoogleSignInOptions.Builder::class)
        every {
            anyConstructed<GoogleSignInOptions.Builder>().build()
        } returns googleSignInOptions

        // When
        val result = authGoogleUseCase.signIn()

        // Then
        result shouldBe signInIntent
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
        val resultFlow: Flow<UserEntity> = authGoogleUseCase.getUser(authorizationCode)
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
        } throws ApiException(Status.RESULT_CANCELED)

        // When
        val resultFlow: Flow<UserEntity> = authGoogleUseCase.getUser(authorizationCode)
        val result = resultFlow.toList()

        // Then
        result.size shouldBe 0
    }
}