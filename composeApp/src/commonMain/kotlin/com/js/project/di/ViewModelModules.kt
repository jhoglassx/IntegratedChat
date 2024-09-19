package com.js.project.di

import com.js.project.ui.auth.AuthViewModel
import com.js.project.ui.chat.ChatViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val viewModelModule = module {
    singleOf(::AuthViewModel)
    singleOf(::ChatViewModel)
}