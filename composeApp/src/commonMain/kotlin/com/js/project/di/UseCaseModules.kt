package com.js.project.di

import com.js.project.domain.usecase.ChatTwitchUseCase
import com.js.project.domain.usecase.ChatTwitchUseCaseImpl
import com.js.project.domain.usecase.ChatYoutubeUseCase
import com.js.project.domain.usecase.ChatYoutubeUseCaseImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val useCaseModules = module {
    singleOf(::ChatTwitchUseCaseImpl).bind<ChatTwitchUseCase>()
    singleOf(::ChatYoutubeUseCaseImpl).bind<ChatYoutubeUseCase>()
}