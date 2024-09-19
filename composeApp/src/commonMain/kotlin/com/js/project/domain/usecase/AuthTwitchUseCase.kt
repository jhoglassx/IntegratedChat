package com.js.project.domain.usecase

import com.js.project.domain.entity.UserEntity
import kotlinx.coroutines.flow.Flow

expect class AuthTwitchUseCase {
    suspend fun signIn(): String?
    suspend fun signOut()
    suspend fun getUser(
        authorizationCode: String,
    ): Flow<UserEntity>
}

