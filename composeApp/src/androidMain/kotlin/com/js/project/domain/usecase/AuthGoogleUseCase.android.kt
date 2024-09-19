package com.js.project.domain.usecase

import Constants.GOOGLE_DESKTOP_REDIRECT_URI
import Constants.GOOGLE_SCOPES
import Constants.GOOGLE_TOKEN
import Constants.GOOGLE_TOKEN_URL
import Constants.GOOGLE_USER_URL
import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.js.project.data.repository.TokenRepository
import com.js.project.data.repository.UserRepository
import com.js.project.domain.entity.UserEntity
import com.js.project.provider.KeysConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last

actual class AuthGoogleUseCase(
    private val context: Context,
    private val tokenRepository: TokenRepository,
    private val userRepository: UserRepository,
) {
    actual suspend fun signIn(): Any? {

        val googleSignInOptions = getGoogleSignInOptions()
        val googleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions)
        val signInIntent = googleSignInClient.signInIntent
        Log.e("AuthClientUseCaseImpl","loginWithGoogle Success: %s $signInIntent")
        return signInIntent
    }

    actual suspend fun signOut() {

    }

    private fun getGoogleSignInOptions() = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestScopes(Scope(GOOGLE_SCOPES))
        .requestIdToken(KeysConfig.googleWebClientId)
        .requestServerAuthCode(KeysConfig.googleWebClientId)
        .build()

    actual suspend fun getUser(
        authorizationCode: String,
    ): Flow<UserEntity> = flow {

        var user: UserEntity? = null
        try {
            val tokenResponse = tokenRepository.fetchToken(
                GOOGLE_TOKEN_URL,
                KeysConfig.googleWebClientId,
                KeysConfig.googleWebClientSecret,
                authorizationCode,
                GOOGLE_DESKTOP_REDIRECT_URI
            ).last()

            user = userRepository.fetchUserGoogle(
                GOOGLE_USER_URL,
                tokenResponse.accessToken,
                KeysConfig.googleWebClientId
            ).last()

            GOOGLE_TOKEN = tokenResponse.accessToken

        } catch (e: ApiException) {

        }
        user?.let {
            emit(it)
        }
    }
}