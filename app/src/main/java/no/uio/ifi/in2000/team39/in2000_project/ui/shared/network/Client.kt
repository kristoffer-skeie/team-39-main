package no.uio.ifi.in2000.team39.in2000_project.ui.shared.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Singleton object that provides a configured HttpClient for network operations.
 * This client is configured to communicate with the specific base URL for the project's backend
 * and includes necessary headers for API authentication.
 */
object NetworkClient {
    private const val BASE_URL = "https://gw-uio.intark.uh-it.no/in2000/"
    private const val API_KEY = "X-Gravitee-API-Key"
    private const val API_KEY_VALUE = "a250d6ca-e181-45ee-9a70-66a7d3791b09"

    val client: HttpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        defaultRequest {
            url(BASE_URL)
            header(API_KEY, API_KEY_VALUE)
        }
    }
}
