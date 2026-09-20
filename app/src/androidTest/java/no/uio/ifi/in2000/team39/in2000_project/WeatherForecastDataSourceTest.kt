package no.uio.ifi.in2000.team39.in2000_project

import androidx.test.ext.junit.runners.AndroidJUnit4
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import no.uio.ifi.in2000.team39.in2000_project.data.weather.WeatherForecastDataSource
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests for the WeatherForecastDataSource that ensure the reliability and correctness of the network interactions
 * and data fetching mechanisms used to retrieve weather data.
 */
@RunWith(AndroidJUnit4::class)
class WeatherForecastDataSourceTest {
    /**
     * Tests the configuration and response status of the HTTP client used to fetch weather data.
     * This test checks if the HTTP client is properly configured and can successfully connect to the server.
     */
    @Test
    fun weatherForecastDataSourceTest() {
        runBlocking {
            val client = HttpClient(CIO) {
                install(ContentNegotiation) {
                    json(Json {
                        ignoreUnknownKeys = true
                    })
                }
                defaultRequest {
                    url("https://gw-uio.intark.uh-it.no/in2000/")
                    header("X-Gravitee-API-Key", "a250d6ca-e181-45ee-9a70-66a7d3791b09")
                }
            }

            val result = client.get("https://gw-uio.intark.uh-it.no/in2000/").status
            Assert.assertEquals(200, result.value)
        }
    }

    /**
     * Tests fetching weather data using valid coordinates to ensure that the data source returns a successful response.
     * This test verifies that the data source correctly processes valid geographic coordinates and retrieves data as expected.
     */
    @Test
    fun testWeatherFetch() {
        runBlocking {
            val mockDataSource = WeatherForecastDataSource()
            val result = mockDataSource.fetchWeather(59.9139f, 10.7522f)
            assertTrue(result.isSuccess)
            assertNotNull(result.getOrNull())
        }
    }

    /**
     * Tests the handling of invalid coordinates to ensure that the data source fails gracefully.
     * This test confirms that the data source recognizes invalid geographic coordinates and handles them appropriately.
     */
    @Test
    fun testInvalidCoordinates() {
        runBlocking {
            val mockDataSource = WeatherForecastDataSource()
            val result = mockDataSource.fetchWeather(1000f, 1000f) // Invalid coordinates
            assertTrue(result.isFailure)
        }
    }
}
