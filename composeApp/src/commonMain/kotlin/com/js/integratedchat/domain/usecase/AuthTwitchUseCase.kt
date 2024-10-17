package com.js.integratedchat.domain.usecase

import com.js.integratedchat.domain.entity.UserEntity
import kotlinx.coroutines.flow.Flow

expect class AuthTwitchUseCase {
    suspend fun signIn(): String?
    suspend fun signOut()
    suspend fun getUser(
        authorizationCode: String,
    ): Flow<UserEntity>
}

