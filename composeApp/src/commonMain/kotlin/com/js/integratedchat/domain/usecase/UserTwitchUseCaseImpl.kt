package com.js.integratedchat.domain.usecase

import Constants.TWITCH_REDIRECT_URI
import Constants.TWITCH_TOKEN
import Constants.TWITCH_TOKEN_URL
import Constants.TWITCH_USER_INFO_URL
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

class UserTwitchUseCaseImpl(
    private val tokenRepository: TokenRepository,
    private val userRepository: UserRepository,
): UserTwitchUseCase {
    override suspend fun getUser(
        authorizationCode: String
    ): Flow<UserEntity> = flow {

        var user: UserEntity? = null

        Logger.info("AuthTwitchUseCase", "getUser -> authorizationCode: $authorizationCode")

        try {
            val tokenResponse = tokenRepository.fetchToken(
                TWITCH_TOKEN_URL,
                Keys.twitchClientId,
                Keys.twitchClientSecret,
                authorizationCode,
                TWITCH_REDIRECT_URI
            ).last()

            user = userRepository.fetchUserTwitch(
                TWITCH_USER_INFO_URL,
                tokenResponse.accessToken,
                Keys.twitchClientId
            ).last()

            Logger.info( "AuthTwitchUseCase", "getUser -> user: $user")

            TWITCH_TOKEN = tokenResponse.accessToken

            Logger.info("AuthTwitchUseCase","getUser -> TWITCH_TOKEN: $TWITCH_TOKEN")

        } catch (e: Exception) {
            Logger.error(tag = "AuthTwitchUseCase", message = "getUser: $e", throwable = e)
        }

        user?.let {
            emit(it)
        }
    }
}