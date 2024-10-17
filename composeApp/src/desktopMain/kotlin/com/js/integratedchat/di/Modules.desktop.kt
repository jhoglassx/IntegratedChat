package com.js.integratedchat.di

import com.js.integratedchat.domain.usecase.AuthGoogleUseCase
import com.js.integratedchat.domain.usecase.AuthTwitchUseCase
import com.js.integratedchat.provider.DispatcherProvider
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformModule = module {
    singleOf(::AuthGoogleUseCase)
    singleOf(::DispatcherProvider)
}