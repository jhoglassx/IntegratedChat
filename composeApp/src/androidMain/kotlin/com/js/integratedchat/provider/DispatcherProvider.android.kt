package com.js.integratedchat.provider

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual class DispatcherProvider {
    actual val MAIN: CoroutineDispatcher = Dispatchers.Default // Use Default para desktop
    actual val IO: CoroutineDispatcher = Dispatchers.IO
}