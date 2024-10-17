package com.js.integratedchat.di

import com.js.integratedchat.domain.usecase.ChatTwitchUseCase
import com.js.integratedchat.domain.usecase.ChatTwitchUseCaseImpl
import com.js.integratedchat.domain.usecase.ChatYoutubeUseCase
import com.js.integratedchat.domain.usecase.ChatYoutubeUseCaseImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val useCaseModules = module {
    singleOf(::ChatTwitchUseCaseImpl).bind<ChatTwitchUseCase>()
    singleOf(::ChatYoutubeUseCaseImpl).bind<ChatYoutubeUseCase>()
}