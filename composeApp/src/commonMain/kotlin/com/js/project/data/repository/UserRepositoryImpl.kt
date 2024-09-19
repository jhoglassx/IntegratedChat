package com.js.project.data.repository

import com.js.project.data.datasource.UserDataSource
import com.js.project.data.entity.toRemote
import com.js.project.domain.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.last

class UserRepositoryImpl(
    private val userDataSource: UserDataSource
): UserRepository {

    override suspend fun fetchUserGoogle(
        userInfoUrl: String,
        accessToken: String,
        clientId: String
    ): Flow<UserEntity> = flow{
        val result =  userDataSource.fetchUserGoogle(
            userInfoUrl = userInfoUrl,
            accessToken = accessToken,
            clientId = clientId
        )

        emit(result.last().toRemote())
    }

    override suspend fun fetchUserTwitch(
        userInfoUrl: String,
        accessToken: String,
        clientId: String
    ): Flow<UserEntity> = flow {
       val result =  userDataSource.fetchUserTwitch(
           userInfoUrl = userInfoUrl,
           accessToken = accessToken,
           clientId = clientId
       )
        emit(result.last().toRemote())
    }
}

