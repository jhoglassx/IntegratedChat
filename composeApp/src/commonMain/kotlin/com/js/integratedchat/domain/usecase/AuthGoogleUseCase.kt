package com.js.integratedchat.domain.usecase

import com.js.integratedchat.domain.entity.UserEntity
import kotlinx.coroutines.flow.Flow

expect class AuthGoogleUseCase {
    suspend fun signIn(): Any?
    suspend fun signOut()
    suspend fun getUser(
        authorizationCode: String,
    ): Flow<UserEntity>
}