package com.js.integratedchat.data.repository

import co.touchlab.kermit.Logger
import com.js.integratedchat.data.datasource.UserDataSource
import com.js.integratedchat.data.entity.UserGoogleRemoteEntity
import com.js.integratedchat.data.entity.UserTwitchRemoteEntity
import com.js.integratedchat.domain.entity.UserEntity
import io.kotest.matchers.shouldBe
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockkObject
import io.mockk.unmockkAll
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import kotlin.test.Test

class UserRepositoryImplTest {

    @MockK
    private lateinit var userDataSource: UserDataSource

    private lateinit var userRepositoryImpl: UserRepositoryImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        userRepositoryImpl = UserRepositoryImpl(
            userDataSource = userDataSource
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `Given Google user info, when fetchUserGoogle is called, then it should return UserEntity flow`() = runTest {
        // Given
        val userInfoUrl = "https://example.com/userinfo"
        val accessToken = "accessToken123"
        val clientId = "clientId123"
        val userEntity = UserEntity(
            id = "userId",
            email = "email",
            name = "userName",
            displayName = "displayName",
            imageUrl = "imageUrl"
        )
        val userGoogleRemoteEntity = UserGoogleRemoteEntity(
            id = "userId",
            email = "email",
            name = "userName",
            imageUrl = "imageUrl",
            displayName = "displayName",
        )
        coEvery {
            userDataSource.fetchUserGoogle(userInfoUrl, accessToken, clientId)
        } returns flowOf(userGoogleRemoteEntity)
        mockkObject(Logger)  // Mock do Logger

        // When
        val result = userRepositoryImpl.fetchUserGoogle(userInfoUrl, accessToken, clientId).toList()

        // Then
        result.size shouldBe 1
        result[0] shouldBe userEntity

        coVerify {
            userDataSource.fetchUserGoogle(userInfoUrl, accessToken, clientId)
        }
    }

    @Test
    fun `Given Twitch user info, when fetchUserTwitch is called, then it should return UserEntity flow`() = runTest {
        // Given
        val userInfoUrl = "https://example.com/userinfo"
        val accessToken = "accessToken123"
        val clientId = "clientId123"
        val userEntity = UserEntity(
            id = "userId",
            email = "email",
            name = "userName",
            displayName = "displayName",
            imageUrl = "imageUrl"
        )
        val userTwitchRemoteEntity = UserTwitchRemoteEntity(
            id = "userId",
            login = "userName",
            displayName = "displayName",
            email = "email",
            profileImageUrl = "imageUrl",
            viewCount = 0,
            broadcasterType = "broadcasterType",
            description = "description",
            offlineImageUrl = "offlineImageUrl",
            type = "type",
            createdAt = "createdAt"
        )

        coEvery {
            userDataSource.fetchUserTwitch(userInfoUrl, accessToken, clientId)
        } returns flowOf(userTwitchRemoteEntity)
        mockkObject(Logger)

        // When
        val result = userRepositoryImpl.fetchUserTwitch(
            userInfoUrl = userInfoUrl,
            accessToken = accessToken,
            clientId = clientId
        ).toList()

        // Then
        result.size shouldBe 1
        result[0] shouldBe userEntity

        coVerify {
            userDataSource.fetchUserTwitch(userInfoUrl, accessToken, clientId)
        }
    }
}