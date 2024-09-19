package com.js.project.service

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpMethod
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.InternalAPI
import kotlinx.serialization.json.Json

class ApiServiceImpl(): ApiService {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        install(Logging) {
            level = LogLevel.BODY
        }
    }

    @OptIn(InternalAPI::class)
    override suspend fun request(
        method: HttpMethod,
        url: String,
        headers: Map<String, String>,
        body: Map<String, String>,
        queryParams: Map<String, String>
    ): HttpResponse {
        return client.request(url) {
            this.method = method
            headers.forEach { (key, value) ->
                header(key, value)
            }
            queryParams.forEach { (key, value) ->
                parameter(key, value)
            }
            if(body.isNotEmpty()){
                this.body = encodedFormParameters(body)
            }
        }
    }

    private fun encodedFormParameters(
        content: Map<String, String>
    ) = content.entries.joinToString("&") { (key, value) ->
        "${key}=${value}"
    }
}