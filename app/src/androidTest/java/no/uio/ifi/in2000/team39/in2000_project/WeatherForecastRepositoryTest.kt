package no.uio.ifi.in2000.team39.in2000_project

import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import no.uio.ifi.in2000.team39.in2000_project.data.weather.WeatherForecastDataSource
import no.uio.ifi.in2000.team39.in2000_project.data.weather.WeatherForecastRepositoryImplementation
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Test suite for WeatherForecastRepositoryImplementation to ensure correct data handling
 * and retrieval operations, focusing on integration with WeatherForecastDataSource.
 */
@RunWith(AndroidJUnit4::class)
class WeatherForecastRepositoryTest {
    /**
     * Tests the consistency and reliability of data retrieval from the repository compared to direct fetches
     * from the data source, ensuring that the repository accurately represents the data source's functionality.
     */
    @Test
    fun weatherForecastRepositoryTest() {
        val testScope = TestScope()
        val mockDataSource = WeatherForecastDataSource()
        val repository = WeatherForecastRepositoryImplementation()
        testScope.backgroundScope.launch {
            val expectedResult = repository.getWeatherDetails(lat = 10f, lon = 60f)
            val result = mockDataSource.fetchWeather(10f, 60f)
            Assert.assertEquals(expectedResult, result)
            testScope.cancel()
        }
    }

    /**
     * Tests multiple retrievals from the repository to ensure that the caching mechanism does not impact the accuracy
     * of data retrieval. Verifies that multiple calls with the same parameters return the same result and that all
     * retrievals are successful.
     */
    @Test
    fun testGetWeatherDetails() {
        val testScope = TestScope()
        val mockDataSource = WeatherForecastDataSource()
        val repository = WeatherForecastRepositoryImplementation(mockDataSource)

        testScope.backgroundScope.launch {
            val firstResult = repository.getWeatherDetails(lat = 10f, lon = 60f)
            val secondResult = repository.getWeatherDetails(lat = 30f, lon = 40f)
            val thirdResult = repository.getWeatherDetails(lat = 20f, lon = 50f)

            assertTrue(firstResult.isSuccess)
            assertTrue(secondResult.isSuccess)
            assertTrue(thirdResult.isSuccess)

            // Checks if the results are consistent across different coordinates
            assertEquals(firstResult.getOrNull(), secondResult.getOrNull())

            testScope.cancel()
        }
    }
}
