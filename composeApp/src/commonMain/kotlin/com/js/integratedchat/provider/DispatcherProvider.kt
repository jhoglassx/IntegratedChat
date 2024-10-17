package com.js.integratedchat.provider

import kotlinx.coroutines.CoroutineDispatcher

expect class DispatcherProvider {
    val MAIN: CoroutineDispatcher
    val IO: CoroutineDispatcher
}