package com.js.integratedchat.service

import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpMethod

interface ApiService {
    suspend fun request(
        method: HttpMethod,
        url: String,
        headers: Map<String, String> = emptyMap(),
        body: Map<String, String?> = emptyMap(),
        queryParams: Map<String, String> = emptyMap()
    ): HttpResponse
}