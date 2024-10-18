package com.js.integratedchat.domain.usecase

import Constants.GOOGLE_REDIRECT_URI
import Constants.GOOGLE_SCOPES
import com.js.integratedchat.data.Keys
import java.net.URLEncoder

actual class AuthGoogleUseCase() {
    actual suspend fun signIn(): Any? {
        val authUrl = "https://accounts.google.com/o/oauth2/auth" +
            "?client_id=${URLEncoder.encode(Keys.googleClientId, "UTF-8")}" +
            "&redirect_uri=${URLEncoder.encode(GOOGLE_REDIRECT_URI, "UTF-8")}" +
            "&response_type=code" +
            "&scope=${URLEncoder.encode(GOOGLE_SCOPES, "UTF-8")}"

        return authUrl
    }

    actual suspend fun signOut() {

    }
}