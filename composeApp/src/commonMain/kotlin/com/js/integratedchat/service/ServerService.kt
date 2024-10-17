package com.js.integratedchat.service

import io.ktor.server.netty.NettyApplicationEngine

interface ServerService{

    fun server(
        port: Int,
        onUriReceived: (String?) -> Unit
    ) : NettyApplicationEngine

}

