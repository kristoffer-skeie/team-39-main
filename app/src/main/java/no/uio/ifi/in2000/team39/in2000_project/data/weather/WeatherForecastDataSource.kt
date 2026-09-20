package no.uio.ifi.in2000.team39.in2000_project.data.weather

import android.util.Log
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.get
import kotlinx.serialization.SerializationException
import no.uio.ifi.in2000.team39.in2000_project.model.weather.WeatherFeature
import no.uio.ifi.in2000.team39.in2000_project.ui.shared.network.NetworkClient

/**
 * DataSource class responsible for fetching weather data from a remote API.
 * Uses the Ktor HTTP client configured for JSON serialization to interact with the specified API.
 */
class WeatherForecastDataSource {
    private val client = NetworkClient.client

    /**
     * Fetches weather data for a specific latitude [lat] and longitude [lon].
     * Returns a [Result] containing [WeatherFeature] on success or an error on failure.
     * Uses structured exception handling to manage different types of errors from network requests.
     */
    suspend fun fetchWeather(
        lat: Float, lon: Float
    ): Result<WeatherFeature> {
        val url = "weatherapi/locationforecast/2.0/complete?lat=$lat&lon=$lon"

        return try {
            val response: WeatherFeature = client.get(url).body()
            Result.success(response)
        } catch (e: Exception) {
            val errorMessage = when (e) {
                is ClientRequestException -> "Client request error: ${e.response.status.description}"
                is ServerResponseException -> "Server response error: ${e.response.status.description}"
                is SerializationException -> "Serialization error: ${e.message}"
                else -> "Error fetching weather data: ${e.message}"
            }

            Log.e("WEATHER_FORECAST_DATA_SOURCE", errorMessage)
            Result.failure(e)
        }
    }
}
