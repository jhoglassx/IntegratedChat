package com.js.project.di

import com.js.project.service.ServerService
import com.js.project.service.ServerServiceImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val manageModule = module {
    singleOf(::ServerServiceImpl).bind<ServerService>()
}