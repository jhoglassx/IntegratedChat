package com.js.integratedchat.di

import com.js.integratedchat.data.datasource.ChatTwitchDataSource
import com.js.integratedchat.data.datasource.ChatTwitchDataSourceImpl
import com.js.integratedchat.data.datasource.ChatYouTubeLiveIdDataSource
import com.js.integratedchat.data.datasource.ChatYouTubeLiveIdDataSourceImpl
import com.js.integratedchat.data.datasource.ChatYoutubeDataSource
import com.js.integratedchat.data.datasource.ChatYoutubeDataSourceImpl
import com.js.integratedchat.data.datasource.TokenDataSource
import com.js.integratedchat.data.datasource.TokenDataSourceImpl
import com.js.integratedchat.data.datasource.UserDataSource
import com.js.integratedchat.data.datasource.UserDataSourceImpl
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