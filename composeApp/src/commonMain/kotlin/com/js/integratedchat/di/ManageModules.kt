package com.js.integratedchat.di

import com.js.integratedchat.service.ServerService
import com.js.integratedchat.service.ServerServiceImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val manageModule = module {
    singleOf(::ServerServiceImpl).bind<ServerService>()
}