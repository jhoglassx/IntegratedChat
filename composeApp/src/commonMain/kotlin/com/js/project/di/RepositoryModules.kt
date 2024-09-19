package com.js.project.di


import com.js.project.data.repository.ChatTwitchRepository
import com.js.project.data.repository.ChatTwitchRepositoryImpl
import com.js.project.data.repository.ChatYouTubeLiveIdRepository
import com.js.project.data.repository.ChatYouTubeLiveIdRepositoryImpl
import com.js.project.data.repository.ChatYoutubeRepository
import com.js.project.data.repository.ChatYoutubeRepositoryImpl
import com.js.project.data.repository.TokenRepository
import com.js.project.data.repository.TokenRepositoryImpl
import com.js.project.data.repository.UserRepository
import com.js.project.data.repository.UserRepositoryImpl
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