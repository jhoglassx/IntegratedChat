package com.js.integratedchat.domain.usecase

import Constants.TWITCH_REDIRECT_URI
import Constants.TWITCH_SCOPES
import Constants.TWITCH_TOKEN
import Constants.TWITCH_TOKEN_URL
import Constants.TWITCH_USER_INFO_URL
import co.touchlab.kermit.Logger
import com.google.android.gms.common.api.ApiException
import com.js.integratedchat.BuildConfig
import com.js.integratedchat.data.repository.TokenRepository
import com.js.integratedchat.data.repository.UserRepository
import com.js.integratedchat.domain.entity.UserEntity
import com.js.integratedchat.ext.error
import com.js.integratedchat.ext.info
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
            "?client_id=${BuildConfig.TWITCH_CLIENT_ID}" +
            "&redirect_uri=$TWITCH_REDIRECT_URI" +
            "&response_type=code" +
            "&scope=$scopes"

        return uri
    }

    actual suspend fun signOut() {}
    actual suspend fun getUser(
        authorizationCode: String
    ): Flow<UserEntity> = flow {

        var user: UserEntity? = null

        Logger.info(tag = "AuthTwitchUseCase", "getUser -> authorizationCode: $authorizationCode")

        try {
            val tokenResponse = tokenRepository.fetchToken(
                TWITCH_TOKEN_URL,
                BuildConfig.TWITCH_CLIENT_ID,
                BuildConfig.TWITCH_CLIENT_SECRET,
                authorizationCode,
                TWITCH_REDIRECT_URI
            ).last()

            user = userRepository.fetchUserTwitch(
                TWITCH_USER_INFO_URL,
                tokenResponse.accessToken,
                BuildConfig.TWITCH_CLIENT_ID
            ).last()

            Logger.info(
                tag = "AuthTwitchUseCase",
                message = "getUser -> user: $user"
            )

            TWITCH_TOKEN = tokenResponse.accessToken

            Logger.info("AuthTwitchUseCase","getUser -> TWITCH_TOKEN: $TWITCH_TOKEN")

        } catch (e: ApiException) {
            Logger.error(
                tag = "AuthTwitchUseCase",
                throwable = e,
                message = "getUser: $e"
            )
        }

        user?.let {
            emit(it)
        }
    }
}