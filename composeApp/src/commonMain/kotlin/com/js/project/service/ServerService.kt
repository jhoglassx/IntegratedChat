package com.js.project.service

import io.ktor.server.netty.NettyApplicationEngine

interface ServerService{

    fun server(
        port: Int,
        onUriReceived: (String?) -> Unit
    ) : NettyApplicationEngine

}

