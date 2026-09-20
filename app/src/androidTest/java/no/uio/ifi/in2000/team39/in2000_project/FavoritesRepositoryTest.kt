package no.uio.ifi.in2000.team39.in2000_project

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000.team39.in2000_project.data.favorite.AppDatabase
import no.uio.ifi.in2000.team39.in2000_project.data.favorite.FavoriteForecastItemDao
import no.uio.ifi.in2000.team39.in2000_project.data.favorite.FavoritesRepository
import no.uio.ifi.in2000.team39.in2000_project.model.favorite.FavoriteForecastItemEntity
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented tests for the FavoritesRepository to ensure it handles CRUD operations correctly.
 * These tests interact with the Room database through the repository to verify the integration
 * of database operations with the app's data model under realistic conditions.
 */
@RunWith(AndroidJUnit4::class)
class FavoritesRepositoryTest {
    private lateinit var database: AppDatabase
    private lateinit var favoritesDao: FavoriteForecastItemDao
    private lateinit var repository: FavoritesRepository

    /**
     * Sets up the environment for each test. This includes creating an in-memory database instance,
     * which is fast and safe for testing because it avoids altering the production database.
     */
    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()

        favoritesDao = database.favoriteForecastItemDao()

        repository = FavoritesRepository(ApplicationProvider.getApplicationContext())
    }

    /**
     * Cleans up and closes the database after each test. This ensures that test data does not persist
     * between tests, providing a clean slate for each test scenario.
     */
    @After
    fun tearDown() {
        database.close()
    }

    /**
     * Tests that a favorite forecast item can be successfully inserted into the database and retrieved,
     * ensuring the data integrity and proper functioning of insert and retrieve operations within the repository.
     */
    @Test
    fun testInsertFavorite() = runBlocking {
        val favorite = FavoriteForecastItemEntity(
            compositeKey = "testKey",
            time = "testTime",
            lat = 0.0,
            lon = 0.0,
            altitude = 0.0,
            groundWindSpeed = 10.0,
            groundWindSpeedUnit = "m/s",
            groundWindSpeedStatus = "normal",
            gustWindSpeed = 15.0,
            gustWindSpeedUnit = "m/s",
            gustWindSpeedStatus = "normal",
            maxWindShear = 20.0,
            maxWindShearUnit = "m/s",
            maxWindShearStatus = "normal",
            maxAirWindSpeed = 25.0,
            maxAirWindSpeedUnit = "m/s",
            maxAirWindSpeedStatus = "normal",
            cloudCoverageHigh = 30.0,
            cloudCoverageHighUnit = "%",
            cloudCoverageHighStatus = "normal",
            cloudCoverageMedium = 35.0,
            cloudCoverageMediumUnit = "%",
            cloudCoverageMediumStatus = "normal",
            cloudCoverageLow = 40.0,
            cloudCoverageLowUnit = "%",
            cloudCoverageLowStatus = "normal",
            fogAreaFraction = 45.0,
            fogAreaFractionUnit = "%",
            fogAreaFractionStatus = "normal",
            precipitationAmount = 50.0,
            precipitationAmountUnit = "mm",
            precipitationAmountStatus = "normal",
            relativeHumidity = 55.0,
            relativeHumidityUnit = "%",
            relativeHumidityStatus = "normal",
            summary = "Test summary"
        )

        repository.insertFavorite(favorite)

        val favorites = repository.getAllFavorites()
        assert(favorites.containsKey("testKey"))
    }

    /**
     * Tests the deletion of a favorite forecast item by its ID, ensuring that the repository correctly
     * handles the removal of data and that the data is no longer accessible post-deletion.
     */
    @Test
    fun testRemoveFavoriteById() = runBlocking {
        val favorite = FavoriteForecastItemEntity(
            compositeKey = "testKey",
            time = "testTime",
            lat = 0.0,
            lon = 0.0,
            altitude = 0.0,
            groundWindSpeed = 10.0,
            groundWindSpeedUnit = "m/s",
            groundWindSpeedStatus = "normal",
            gustWindSpeed = 15.0,
            gustWindSpeedUnit = "m/s",
            gustWindSpeedStatus = "normal",
            maxWindShear = 20.0,
            maxWindShearUnit = "m/s",
            maxWindShearStatus = "normal",
            maxAirWindSpeed = 25.0,
            maxAirWindSpeedUnit = "m/s",
            maxAirWindSpeedStatus = "normal",
            cloudCoverageHigh = 30.0,
            cloudCoverageHighUnit = "%",
            cloudCoverageHighStatus = "normal",
            cloudCoverageMedium = 35.0,
            cloudCoverageMediumUnit = "%",
            cloudCoverageMediumStatus = "normal",
            cloudCoverageLow = 40.0,
            cloudCoverageLowUnit = "%",
            cloudCoverageLowStatus = "normal",
            fogAreaFraction = 45.0,
            fogAreaFractionUnit = "%",
            fogAreaFractionStatus = "normal",
            precipitationAmount = 50.0,
            precipitationAmountUnit = "mm",
            precipitationAmountStatus = "normal",
            relativeHumidity = 55.0,
            relativeHumidityUnit = "%",
            relativeHumidityStatus = "normal",
            summary = "Test summary"
        )

        repository.insertFavorite(favorite)
        assertEquals(0.0, favorite.lat)
        assertEquals(0.0, favorite.lon)
        assertEquals(0.0, favorite.altitude)
        assertEquals(10.0, favorite.groundWindSpeed)
        repository.removeFavoriteById("testKey")

        val favorites = repository.getAllFavorites()
        assert(favorites.isEmpty())
    }

    /**
     * Tests the robustness of the deletion operation by attempting to remove a non-existing favorite.
     * This test verifies that removing a non-existent entry does not affect the existing data.
     */
    @Test
    fun testRemoveNonExistingFavorite() = runBlocking {
        val favorite = FavoriteForecastItemEntity(
            compositeKey = "testKey",
            time = "testTime",
            lat = 0.0,
            lon = 0.0,
            altitude = 0.0,
            groundWindSpeed = 10.0,
            groundWindSpeedUnit = "m/s",
            groundWindSpeedStatus = "normal",
            gustWindSpeed = 15.0,
            gustWindSpeedUnit = "m/s",
            gustWindSpeedStatus = "normal",
            maxWindShear = 20.0,
            maxWindShearUnit = "m/s",
            maxWindShearStatus = "normal",
            maxAirWindSpeed = 25.0,
            maxAirWindSpeedUnit = "m/s",
            maxAirWindSpeedStatus = "normal",
            cloudCoverageHigh = 30.0,
            cloudCoverageHighUnit = "%",
            cloudCoverageHighStatus = "normal",
            cloudCoverageMedium = 35.0,
            cloudCoverageMediumUnit = "%",
            cloudCoverageMediumStatus = "normal",
            cloudCoverageLow = 40.0,
            cloudCoverageLowUnit = "%",
            cloudCoverageLowStatus = "normal",
            fogAreaFraction = 45.0,
            fogAreaFractionUnit = "%",
            fogAreaFractionStatus = "normal",
            precipitationAmount = 50.0,
            precipitationAmountUnit = "mm",
            precipitationAmountStatus = "normal",
            relativeHumidity = 55.0,
            relativeHumidityUnit = "%",
            relativeHumidityStatus = "normal",
            summary = "Test summary"
        )

        // Insert a favorite
        repository.insertFavorite(favorite)

        // Attempt to remove a non-existing favorite
        repository.removeFavoriteById("nonExistingKey")

        // Ensure that the map of favorites remains unchanged
        val favorites = repository.getAllFavorites()
        assertTrue(favorites.containsKey("testKey"))
    }
}