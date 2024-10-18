package com.js.integratedchat.domain.usecase

expect class AuthGoogleUseCase {
    suspend fun signIn(): Any?
    suspend fun signOut()
}