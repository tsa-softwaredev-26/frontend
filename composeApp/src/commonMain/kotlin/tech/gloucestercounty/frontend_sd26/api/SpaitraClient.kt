package tech.gloucestercounty.frontend_sd26.api

import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object SpaitraClient {

    private const val BASE_URL = "https://nre5bjw44wddpu2zjg4fe4iehq.srv.us/"
    const val API_KEY = "api-key"

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        install(Logging) {
            level = LogLevel.BODY
        }
        defaultRequest {
            header("X-API-Key", API_KEY)
        }
    }

    private val ktorfit = Ktorfit.Builder()
        .baseUrl(BASE_URL)
        .httpClient(httpClient)
        .build()

    val spaitraApi: SpaitraApi = ktorfit.create()
}