package com.js.integratedchat.di

import com.js.integratedchat.ui.auth.AuthViewModel
import com.js.integratedchat.ui.chat.ChatViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val viewModelModule = module {
    singleOf(::AuthViewModel)
    singleOf(::ChatViewModel)
}