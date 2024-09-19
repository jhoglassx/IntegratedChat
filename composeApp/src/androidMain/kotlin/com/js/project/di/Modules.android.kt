package com.js.project.di

import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.js.project.domain.usecase.AuthGoogleUseCase
import com.js.project.domain.usecase.AuthTwitchUseCase
import com.js.project.provider.DispatcherProvider
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val platformModule = module {
    singleOf(::AuthGoogleUseCase)
    singleOf(::AuthTwitchUseCase)
    singleOf(::DispatcherProvider)
    single<SignInClient>{
        Identity.getSignInClient(androidContext())
    }
}
