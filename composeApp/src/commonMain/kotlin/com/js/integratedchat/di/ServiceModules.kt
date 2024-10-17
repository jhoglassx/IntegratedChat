package com.js.integratedchat.di

import com.js.integratedchat.provider.BadgeCache
import com.js.integratedchat.provider.BadgeCacheImpl
import com.js.integratedchat.service.ApiService
import com.js.integratedchat.service.ApiServiceImpl
import com.js.integratedchat.service.TwitchChatService
import com.js.integratedchat.service.TwitchChatServiceImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val serviceModule = module {
    singleOf(::TwitchChatServiceImpl).bind<TwitchChatService>()
    singleOf(::ApiServiceImpl).bind<ApiService>()
    singleOf(::BadgeCacheImpl).bind<BadgeCache>()
}