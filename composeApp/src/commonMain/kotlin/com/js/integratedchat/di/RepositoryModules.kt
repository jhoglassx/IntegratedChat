package com.js.integratedchat.di


import com.js.integratedchat.data.repository.ChatTwitchRepository
import com.js.integratedchat.data.repository.ChatTwitchRepositoryImpl
import com.js.integratedchat.data.repository.ChatYouTubeLiveIdRepository
import com.js.integratedchat.data.repository.ChatYouTubeLiveIdRepositoryImpl
import com.js.integratedchat.data.repository.ChatYoutubeRepository
import com.js.integratedchat.data.repository.ChatYoutubeRepositoryImpl
import com.js.integratedchat.data.repository.TokenRepository
import com.js.integratedchat.data.repository.TokenRepositoryImpl
import com.js.integratedchat.data.repository.UserRepository
import com.js.integratedchat.data.repository.UserRepositoryImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val repositoryModules = module {
    singleOf(::UserRepositoryImpl).bind<UserRepository>()
    singleOf(::ChatTwitchRepositoryImpl).bind<ChatTwitchRepository>()
    singleOf(::ChatYoutubeRepositoryImpl).bind<ChatYoutubeRepository>()
    singleOf(::ChatYouTubeLiveIdRepositoryImpl).bind<ChatYouTubeLiveIdRepository>()
    singleOf(::TokenRepositoryImpl).bind<TokenRepository>()
}