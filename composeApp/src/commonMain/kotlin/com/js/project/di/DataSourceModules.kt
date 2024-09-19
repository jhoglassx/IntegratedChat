package com.js.project.di

import com.js.project.data.datasource.ChatTwitchDataSource
import com.js.project.data.datasource.ChatTwitchDataSourceImpl
import com.js.project.data.datasource.ChatYouTubeLiveIdDataSource
import com.js.project.data.datasource.ChatYouTubeLiveIdDataSourceImpl
import com.js.project.data.datasource.ChatYoutubeDataSource
import com.js.project.data.datasource.ChatYoutubeDataSourceImpl
import com.js.project.data.datasource.TokenDataSource
import com.js.project.data.datasource.TokenDataSourceImpl
import com.js.project.data.datasource.UserDataSource
import com.js.project.data.datasource.UserDataSourceImpl
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

val dataSourceModules = module {
    singleOf(::UserDataSourceImpl).bind<UserDataSource>()
    singleOf(::ChatTwitchDataSourceImpl).bind<ChatTwitchDataSource>()
    singleOf(::ChatYoutubeDataSourceImpl).bind<ChatYoutubeDataSource>()
    singleOf(::ChatYouTubeLiveIdDataSourceImpl).bind<ChatYouTubeLiveIdDataSource>()
    singleOf(::TokenDataSourceImpl).bind<TokenDataSource>()
}