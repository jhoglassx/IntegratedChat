package com.js.integratedchat.domain.usecase

import Constants.GOOGLE_REDIRECT_URI
import Constants.GOOGLE_SCOPES
import Constants.GOOGLE_TOKEN
import Constants.GOOGLE_TOKEN_URL
import Constants.GOOGLE_USER_URL
import android.content.Context
import co.touchlab.kermit.Logger
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.js.integratedchat.BuildConfig
import com.js.integratedchat.data.repository.TokenRepository
import com.js.integratedchat.data.repository.UserRepository
import com.js.integratedchat.domain.entity.UserEntity
import com.js.integratedchat.ext.info
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
        Logger.info("AuthClientUseCaseImpl","loginWithGoogle Success: %s $signInIntent")
        return signInIntent
    }

    actual suspend fun signOut() {

    }

    private fun getGoogleSignInOptions() = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestScopes(Scope(GOOGLE_SCOPES))
        .requestIdToken(BuildConfig.GOOGLE_WEB_CLIENT_ID)
        .requestServerAuthCode(BuildConfig.GOOGLE_WEB_CLIENT_ID)
        .build()

    actual suspend fun getUser(
        authorizationCode: String,
    ): Flow<UserEntity> = flow {

        var user: UserEntity? = null
        try {
            val tokenResponse = tokenRepository.fetchToken(
                tokenUrl = GOOGLE_TOKEN_URL,
                clientId = BuildConfig.GOOGLE_WEB_CLIENT_ID,
                clientSecret = BuildConfig.GOOGLE_WEB_CLIENT_SECRET,
                authorizationCode = authorizationCode,
                redirectUri = GOOGLE_REDIRECT_URI
            ).last()

            user = userRepository.fetchUserGoogle(
                GOOGLE_USER_URL,
                tokenResponse.accessToken,
                BuildConfig.GOOGLE_WEB_CLIENT_ID
            ).last()

            GOOGLE_TOKEN = tokenResponse.accessToken

        } catch (e: ApiException) {

        }
        user?.let {
            emit(it)
        }
    }
}