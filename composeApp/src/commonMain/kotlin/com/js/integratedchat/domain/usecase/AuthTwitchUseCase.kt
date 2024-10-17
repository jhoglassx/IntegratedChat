package com.js.integratedchat.domain.usecase

interface AuthTwitchUseCase {
    suspend fun signIn(): String?
    suspend fun signOut()
}

