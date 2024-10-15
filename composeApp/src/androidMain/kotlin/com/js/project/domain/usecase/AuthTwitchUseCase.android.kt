package com.js.project.domain.usecase

import Constants.TWITCH_DESKTOP_REDIRECT_URI
import Constants.TWITCH_SCOPES
import Constants.TWITCH_TOKEN
import Constants.TWITCH_TOKEN_URL
import Constants.TWITCH_USER_INFO_URL
import co.touchlab.kermit.Logger
import com.google.android.gms.common.api.ApiException
import com.js.project.data.repository.TokenRepository
import com.js.project.data.repository.UserRepository
import com.js.project.domain.entity.UserEntity
import com.js.project.provider.KeysConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last
import java.net.URLEncoder

actual class AuthTwitchUseCase(
    private val tokenRepository: TokenRepository,
    private val userRepository: UserRepository,
){

    actual suspend fun signIn(): String? {
        val scopes: String = URLEncoder.encode(listOf(TWITCH_SCOPES).joinToString(" "), "UTF-8")

        val uri = "https://id.twitch.tv/oauth2/authorize" +
            "?client_id=${KeysConfig.twitchClientId}" +
            "&redirect_uri=$TWITCH_DESKTOP_REDIRECT_URI" +
            "&response_type=code" +
            "&scope=$scopes"

        return uri
    }

    actual suspend fun signOut() {}
    actual suspend fun getUser(
        authorizationCode: String
    ): Flow<UserEntity> = flow {

        var user: UserEntity? = null

        Logger.i(
            tag = "AuthTwitchUseCase", Throwable(authorizationCode)
        ) {
            "getUser -> authorizationCode: $authorizationCode"
        }

        try {
            val tokenResponse = tokenRepository.fetchToken(
                TWITCH_TOKEN_URL,
                KeysConfig.twitchClientId,
                KeysConfig.twitchClientSecret,
                authorizationCode,
                TWITCH_DESKTOP_REDIRECT_URI
            ).last()

            user = userRepository.fetchUserTwitch(
                TWITCH_USER_INFO_URL,
                tokenResponse.accessToken,
                KeysConfig.twitchClientId
            ).last()

            Logger.i(
                tag = "AuthTwitchUseCase", Throwable(user.toString())
            ) {
                "getUser -> user: $user"
            }

            TWITCH_TOKEN = tokenResponse.accessToken

            Logger.i(
                tag = "AuthTwitchUseCase", Throwable(TWITCH_TOKEN)
            ) {
                "getUser -> TWITCH_TOKEN: $TWITCH_TOKEN"
            }

        } catch (e: ApiException) {
            Logger.e(
                tag = "AuthTwitchUseCase", e
            ) {
                "getUser: $e"
            }
        }

        user?.let {
            emit(it)
        }
    }
}