package no.uio.ifi.in2000.team39.in2000_project.data.favorite

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import no.uio.ifi.in2000.team39.in2000_project.model.favorite.FavoriteForecastItemEntity

/**
 * Manages the database operations for favorite forecast items.
 * Maps Kotlin functions to SQL queries for Room database interactions.
 */
@Dao
interface FavoriteForecastItemDao {
    /**
     * Inserts a new [favoriteForecastItem] or updates an existing one if it already exists.
     * The operation uses a strategy where conflicts cause the existing entry to be replaced.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favoriteForecastItem: FavoriteForecastItemEntity)

    /**
     * Fetches all favorite forecast items from the database.
     * Returns a list sorted by time, latitude, longitude, and altitude in descending order.
     */
    @Query("SELECT * FROM favorites_forecast ORDER BY time, lat, lon, altitude DESC")
    suspend fun getAllFavorites(): List<FavoriteForecastItemEntity>

    /**
     * Removes a favorite forecast item identified by the unique [id].
     */
    @Query("DELETE FROM favorites_forecast WHERE compositeKey = :id")
    suspend fun removeFavoriteById(id: String)
}
