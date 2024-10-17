package com.js.integratedchat.domain.usecase

import Constants.TWITCH_REDIRECT_URI
import Constants.TWITCH_SCOPES
import com.js.integratedchat.BuildConfig
import java.net.URLEncoder

class AuthTwitchUseCaseImpl(): AuthTwitchUseCase {
    override suspend fun signIn(): String? {
        val scopes: String = URLEncoder.encode(listOf(TWITCH_SCOPES).joinToString(" "), "UTF-8")

        val uri = "https://id.twitch.tv/oauth2/authorize" +
            "?client_id=${BuildConfig.TWITCH_CLIENT_ID}" +
            "&redirect_uri=$TWITCH_REDIRECT_URI" +
            "&response_type=code" +
            "&scope=$scopes"

        return uri
    }

    override suspend fun signOut(
    ) {
        println("User signed out")
    }
}