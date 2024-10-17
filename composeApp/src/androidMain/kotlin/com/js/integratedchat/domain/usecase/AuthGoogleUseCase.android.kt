package com.js.integratedchat.domain.usecase

import Constants.GOOGLE_SCOPES
import android.content.Context
import co.touchlab.kermit.Logger
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.Scope
import com.js.integratedchat.data.Keys
import com.js.integratedchat.ext.info

actual class AuthGoogleUseCase(
    private val context: Context
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
        .requestIdToken(Keys.googleClientId)
        .requestServerAuthCode(Keys.googleClientId)
        .build()
}