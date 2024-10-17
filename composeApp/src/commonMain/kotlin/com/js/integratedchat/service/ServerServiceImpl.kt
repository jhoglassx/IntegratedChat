package com.js.integratedchat.service

import io.ktor.http.ContentType
import io.ktor.server.application.call
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

class ServerServiceImpl(): ServerService {
    override fun server(
        port: Int,
        onUriReceived: (String?) -> Unit
    ) = embeddedServer(Netty, port = port) {
        routing {
            get("/callback") {
                val code = call.request.queryParameters["code"]
                if (!code.isNullOrEmpty()) {
                    call.respondText(
                        "Authorization successful! You can close this window.",ContentType.Text.Plain
                    )
                    onUriReceived(code)
                } else {
                    call.respondText(
                        "Authorization failed. No code received.",
                        ContentType.Text.Plain
                    )
                }
            }
        }
    }.start(wait = false)
}

