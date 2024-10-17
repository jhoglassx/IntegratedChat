package com.js.integratedchat.di

import com.js.integratedchat.domain.usecase.AuthTwitchUseCase
import com.js.integratedchat.domain.usecase.AuthTwitchUseCaseImpl
import com.js.integratedchat.domain.usecase.UserGoogleUseCase
import com.js.integratedchat.domain.usecase.UserGoogleUseCaseImpl
import com.js.integratedchat.domain.usecase.ChatTwitchUseCase
import com.js.integratedchat.domain.usecase.ChatTwitchUseCaseImpl
import com.js.integratedchat.domain.usecase.ChatYoutubeUseCase
import com.js.integratedchat.domain.usecase.ChatYoutubeUseCaseImpl
import com.js.integratedchat.domain.usecase.UserTwitchUseCase
import com.js.integratedchat.domain.usecase.UserTwitchUseCaseImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val useCaseModules = module {
    singleOf(::ChatTwitchUseCaseImpl).bind<ChatTwitchUseCase>()
    singleOf(::ChatYoutubeUseCaseImpl).bind<ChatYoutubeUseCase>()
    singleOf(::UserGoogleUseCaseImpl).bind<UserGoogleUseCase>()
    singleOf(::UserTwitchUseCaseImpl).bind<UserTwitchUseCase>()
    singleOf(::AuthTwitchUseCaseImpl).bind<AuthTwitchUseCase>()
}