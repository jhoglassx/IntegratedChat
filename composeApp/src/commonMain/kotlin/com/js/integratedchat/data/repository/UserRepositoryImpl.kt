package com.js.integratedchat.data.repository

import co.touchlab.kermit.Logger
import com.js.integratedchat.data.datasource.UserDataSource
import com.js.integratedchat.data.entity.toRemote
import com.js.integratedchat.domain.entity.UserEntity
import com.js.integratedchat.ext.info
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
        ).last().toRemote()

        Logger.info(
            tag = "UserRepositoryImpl",
            message = "fetchUserGoogle -> result: $result"
        )

        emit(result)
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
       ).last().toRemote()

        Logger.info(
            tag = "UserRepositoryImpl",
            message = "fetchUserTwitch -> result: $result"
        )

        emit(result)
    }
}

