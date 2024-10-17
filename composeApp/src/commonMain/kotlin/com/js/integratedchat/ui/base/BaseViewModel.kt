package com.js.integratedchat.ui.base

import com.js.integratedchat.provider.DispatcherProvider
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope

open class BaseViewModel(
    dispatcherProvider: DispatcherProvider
) {
    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        println("Caught $exception")
    }
    val viewModelScope = CoroutineScope(dispatcherProvider.MAIN + exceptionHandler)
}