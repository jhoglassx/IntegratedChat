package com.js.integratedchat.domain.usecase

import Constants.GOOGLE_DESKTOP_REDIRECT_URI
import Constants.GOOGLE_SCOPES
import Constants.GOOGLE_TOKEN
import Constants.GOOGLE_TOKEN_URL
import Constants.GOOGLE_USER_URL
import com.js.integratedchat.BuildConfig
import com.js.integratedchat.data.repository.TokenRepository
import com.js.integratedchat.data.repository.UserRepository
import com.js.integratedchat.domain.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import java.net.URLEncoder

actual class AuthGoogleUseCase(
    private val tokenRepository: TokenRepository,
    private val userRepository: UserRepository,
) {
    actual suspend fun signIn(): Any? {
        val authUrl = "https://accounts.google.com/o/oauth2/auth" +
            "?client_id=${URLEncoder.encode(BuildConfig.GOOGLE_DESKTOP_CLIENT_ID, "UTF-8")}" +
            "&redirect_uri=${URLEncoder.encode(GOOGLE_DESKTOP_REDIRECT_URI, "UTF-8")}" +
            "&response_type=code" +
            "&scope=${URLEncoder.encode(GOOGLE_SCOPES, "UTF-8")}"

        return authUrl
    }

    actual suspend fun signOut() {

    }

    actual suspend fun getUser(
        authorizationCode: String,
    ): Flow<UserEntity> = flow{

        var user: UserEntity? = null

        try {
            val tokenResponse = tokenRepository.fetchToken(
                GOOGLE_TOKEN_URL,
                BuildConfig.GOOGLE_DESKTOP_CLIENT_ID,
                BuildConfig.GOOGLE_DESKTOP_CLIENT_SECRET,
                authorizationCode,
                GOOGLE_DESKTOP_REDIRECT_URI
            ).last()

            user = userRepository.fetchUserGoogle(
                GOOGLE_USER_URL,
                tokenResponse.accessToken,
                BuildConfig.GOOGLE_DESKTOP_CLIENT_ID
            ).last()

            GOOGLE_TOKEN = tokenResponse.accessToken

        } catch (e: Exception) {

        }

        user?.let {
            emit(it)
        }
    }
}