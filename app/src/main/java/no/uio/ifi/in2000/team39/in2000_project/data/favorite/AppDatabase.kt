package no.uio.ifi.in2000.team39.in2000_project.data.favorite

import androidx.room.Database
import androidx.room.RoomDatabase
import no.uio.ifi.in2000.team39.in2000_project.model.favorite.FavoriteForecastItemEntity

/**
 * Main database for the application, built using Room.
 * Defines the database configuration and serves as the main access point to the persisted data.
 */
@Database(entities = [FavoriteForecastItemEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    /**
     * Use this method to get an instance of [FavoriteForecastItemDao] to perform database operations.
     */
    abstract fun favoriteForecastItemDao(): FavoriteForecastItemDao
}
