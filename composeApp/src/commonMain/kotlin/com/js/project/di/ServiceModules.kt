package com.js.project.di

import com.js.project.provider.BadgeCache
import com.js.project.provider.BadgeCacheImpl
import com.js.project.service.ApiService
import com.js.project.service.ApiServiceImpl
import com.js.project.service.TwitchChatService
import com.js.project.service.TwitchChatServiceImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val serviceModule = module {
    singleOf(::TwitchChatServiceImpl).bind<TwitchChatService>()
    singleOf(::ApiServiceImpl).bind<ApiService>()
    singleOf(::BadgeCacheImpl).bind<BadgeCache>()
}