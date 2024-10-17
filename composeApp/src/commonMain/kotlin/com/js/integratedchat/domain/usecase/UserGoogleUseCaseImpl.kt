package com.js.integratedchat.domain.usecase

import Constants.GOOGLE_REDIRECT_URI
import Constants.GOOGLE_TOKEN
import Constants.GOOGLE_TOKEN_URL
import Constants.GOOGLE_USER_URL
import co.touchlab.kermit.Logger
import com.js.integratedchat.data.Keys
import com.js.integratedchat.data.repository.TokenRepository
import com.js.integratedchat.data.repository.UserRepository
import com.js.integratedchat.domain.entity.UserEntity
import com.js.integratedchat.ext.error
import com.js.integratedchat.ext.info
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last

class UserGoogleUseCaseImpl(
    private val tokenRepository: TokenRepository,
    private val userRepository: UserRepository,
):UserGoogleUseCase {

    override suspend fun getUser(
        authorizationCode: String,
    ): Flow<UserEntity> = flow {

        var user: UserEntity? = null

        Logger.info(tag = "AuthGoogleUseCase", "getUser -> authorizationCode: $authorizationCode")

        try {
            val tokenResponse = tokenRepository.fetchToken(
                tokenUrl = GOOGLE_TOKEN_URL,
                clientId = Keys.googleClientId,
                clientSecret = Keys.googleClientSecret,
                authorizationCode = authorizationCode,
                redirectUri = GOOGLE_REDIRECT_URI
            ).last()

            user = userRepository.fetchUserGoogle(
                GOOGLE_USER_URL,
                tokenResponse.accessToken,
                Keys.googleClientId
            ).last()

            Logger.info( "AuthGoogleUserUseCaseImpl", "getUser -> user: $user")

            GOOGLE_TOKEN = tokenResponse.accessToken

            Logger.info("AuthGoogleUserUseCaseImpl","getUser -> GOOGLE_TOKEN: $GOOGLE_TOKEN")

        } catch (e: Exception) {
            Logger.error(tag = "AuthGoogleUserUseCaseImpl", message = "getUser: $e", throwable = e)
        }

        user?.let {
            emit(it)
        }
    }
}