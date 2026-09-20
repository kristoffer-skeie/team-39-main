package no.uio.ifi.in2000.team39.in2000_project.data.weather

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import no.uio.ifi.in2000.team39.in2000_project.model.weather.WeatherFeature

interface WeatherForecastRepository {
    suspend fun getWeatherDetails(
        lat: Float, lon: Float
    ): Result<WeatherFeature>
}

/**
 * Implementation of [WeatherForecastRepository] that fetches weather data from a remote source.
 * Incorporates simple caching to reduce network calls for recently fetched data.
 */
class WeatherForecastRepositoryImplementation(
    private val weatherForecastDataSource: WeatherForecastDataSource = WeatherForecastDataSource(),
) : WeatherForecastRepository {
    private val _cachedWeatherDetails = MutableStateFlow<Result<WeatherFeature>?>(null)
    private val cachedWeatherDetails = _cachedWeatherDetails.asStateFlow()

    /**
     * Gets weather details for given coordinates ([lat], [lon]).
     * Uses cached data if available to minimize network requests.
     * Fetches new data if cache is empty or outdated.
     * Handles errors and provides detailed information on failures.
     *
     * Returns a [Result] with [WeatherFeature] on success or an error.
     */
    override suspend fun getWeatherDetails(
        lat: Float, lon: Float
    ): Result<WeatherFeature> {
        cachedWeatherDetails.value?.let {
            return it
        }

        return try {
            val feature = weatherForecastDataSource.fetchWeather(lat, lon).getOrThrow()
            val result = Result.success(feature)
            _cachedWeatherDetails.value = result
            result
        } catch (e: Exception) {
            val errorResult = Result.failure<WeatherFeature>(e)
            _cachedWeatherDetails.value = errorResult
            errorResult
        }
    }
}
