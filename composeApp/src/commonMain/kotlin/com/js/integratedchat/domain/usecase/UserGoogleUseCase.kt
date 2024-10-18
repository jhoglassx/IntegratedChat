package com.js.integratedchat.domain.usecase

import com.js.integratedchat.domain.entity.UserEntity
import kotlinx.coroutines.flow.Flow

interface UserGoogleUseCase {
    suspend fun getUser(authorizationCode: String): Flow<UserEntity>
}