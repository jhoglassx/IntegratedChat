package com.js.project.ui.auth

import com.js.project.domain.entity.UserEntity
import com.js.project.domain.usecase.AuthGoogleUseCase
import com.js.project.domain.usecase.AuthTwitchUseCase
import com.js.project.provider.DispatcherProvider
import com.js.project.ui.auth.model.AuthAction
import io.kotest.matchers.shouldBe
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import kotlin.test.Test


@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private lateinit var dispatcherProvider: DispatcherProvider
    @MockK
    private lateinit var authGoogleUseCase: AuthGoogleUseCase
    @MockK
    private lateinit var authTwitchUseCase: AuthTwitchUseCase

    private val testScheduler = TestCoroutineScheduler()
    private val testDispatcher = StandardTestDispatcher(testScheduler)

    private lateinit var viewModel: AuthViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        dispatcherProvider = mockk(relaxed = true)
        coEvery { dispatcherProvider.MAIN } returns testDispatcher
        coEvery { dispatcherProvider.IO } returns testDispatcher

        Dispatchers.setMain(testDispatcher)

        viewModel = AuthViewModel(
            dispatcherProvider = dispatcherProvider,
            authGoogleUseCase = authGoogleUseCase,
            authTwitchUseCase = authTwitchUseCase
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `onAction with GetGoogleSignInIntent should update uiState with google sign-in intent`() = runTest {
        // Given
        val intent = "google_sign_in_intent"
        coEvery { authGoogleUseCase.signIn() } returns intent

        // When
        viewModel.onAction(AuthAction.GetGoogleSignInIntent)
        advanceUntilIdle()

        // Then
        viewModel.uiState.first().authGoogleIntent shouldBe intent
    }

    @Test
    fun `onAction with GetTwitchSignInIntent should update uiState with twitch sign-in intent`() = runTest {
        // Given
        val intent = "twitch_sign_in_intent"
        coEvery { authTwitchUseCase.signIn() } returns intent

        // When
        viewModel.onAction(AuthAction.GetTwitchSignInIntent)
        advanceUntilIdle()

        // Then
        viewModel.uiState.first().authTwitchIntent shouldBe intent
    }

    @Test
    fun `onAction with GetGoogleUser should update uiState with google user`() = runTest {
        // Given
        val authorizationCode = "auth_code"
        val user = UserEntity(
            id = "id",
            email = "email",
            name = "name",
            displayName = "name",
            imageUrl = "imageIrl"
        )

        coEvery {
            authGoogleUseCase.getUser(authorizationCode)
        } returns flowOf(user)

        // When
        viewModel.onAction(AuthAction.GetGoogleUser(authorizationCode))
        advanceUntilIdle()

        // Then
        viewModel.uiState.first().userGoggle shouldBe user
    }

    @Test
    fun `onAction with GetTwitchUser should update uiState with twitch user`() = runTest {
        // Given
        val authorizationCode = "auth_code"
        val user = UserEntity(
            id = "id",
            email = "email",
            name = "name",
            displayName = "name",
            imageUrl = "imageIrl"
        )
        coEvery { authTwitchUseCase.getUser(authorizationCode) } returns flowOf(user)

        // When
        viewModel.onAction(AuthAction.GetTwitchUser(authorizationCode))
        advanceUntilIdle()

        // Then
        viewModel.uiState.first().userTwitch shouldBe user
    }

    @Test
    fun `onAction with SignOut should call signOut on both use cases`() = runTest {
        // Given
        coEvery { authGoogleUseCase.signOut() } returns Unit
        coEvery { authTwitchUseCase.signOut() } returns Unit

        // When
        viewModel.onAction(AuthAction.SignOut)
        advanceUntilIdle()

        // Then
        coVerify { authGoogleUseCase.signOut() }
        coVerify { authTwitchUseCase.signOut() }
    }
}