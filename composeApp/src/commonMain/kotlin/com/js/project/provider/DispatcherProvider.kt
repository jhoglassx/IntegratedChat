package com.js.project.provider

import kotlinx.coroutines.CoroutineDispatcher

expect class DispatcherProvider {
    val MAIN: CoroutineDispatcher
    val IO: CoroutineDispatcher
}