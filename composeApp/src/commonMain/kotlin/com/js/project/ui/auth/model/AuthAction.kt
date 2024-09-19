package com.js.project.ui.auth.model

sealed class AuthAction {
    data object GetGoogleSignInIntent: AuthAction()
    data object GetTwitchSignInIntent: AuthAction()
    data class GetTwitchUser(
        val authorizationCode: String?
    ): AuthAction()
    data class GetGoogleUser(
        val authorizationCode: String?
    ): AuthAction()
    data object SignOut: AuthAction()
}