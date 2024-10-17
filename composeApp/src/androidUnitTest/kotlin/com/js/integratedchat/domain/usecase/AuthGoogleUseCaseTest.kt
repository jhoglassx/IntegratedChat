package com.js.integratedchat.domain.usecase

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.text.TextUtils
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import io.kotest.matchers.shouldBe
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import kotlin.test.Test

class AuthGoogleUseCaseTest {

    @MockK
    private lateinit var context: Context

    @MockK
    private lateinit var packageManager: PackageManager

    private lateinit var authGoogleUseCase: AuthGoogleUseCase


    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        authGoogleUseCase = AuthGoogleUseCase(context)
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
}