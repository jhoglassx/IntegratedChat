package com.js.integratedchat.ext

import co.touchlab.kermit.Logger

// Função de extensão para logging de informações
fun Logger.info(tag: String, message: String) {
    this.i(tag = tag, message = { message })
}

// Função de extensão para logging de erros
fun Logger.error(tag: String, message: String, throwable: Throwable? = null) {
    this.e(tag = tag, throwable = throwable, message = { message })
}