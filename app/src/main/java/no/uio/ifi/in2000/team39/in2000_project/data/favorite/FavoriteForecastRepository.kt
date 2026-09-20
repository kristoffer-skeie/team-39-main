package no.uio.ifi.in2000.team39.in2000_project.data.favorite

import android.app.Application
import no.uio.ifi.in2000.team39.in2000_project.DatabaseProvider
import no.uio.ifi.in2000.team39.in2000_project.model.favorite.FavoriteForecastItemEntity

/**
 * Manages storage and retrieval of favorite forecast items in the database.
 * Uses the application context to access the database via a DAO.
 * Supports fetching, adding/updating, and removing favorites.
 */
class FavoritesRepository(application: Application) {
    private val favoritesDao = DatabaseProvider.getDatabase(application).favoriteForecastItemDao()

    /**
     * Retrieves all favorite forecast items from the database and returns them as a map.
     * The map keys are the composite keys of the items, and the values are the favorite items.
     */
    suspend fun getAllFavorites(): Map<String, FavoriteForecastItemEntity> {
        return favoritesDao.getAllFavorites().associateBy { it.compositeKey }
    }

    /**
     * Adds or updates a [favorite] forecast item in the database.
     * Replaces any existing item with the same composite key.
     */
    suspend fun insertFavorite(favorite: FavoriteForecastItemEntity) {
        favoritesDao.insertFavorite(favorite)
    }

    /**
     * Deletes a favorite forecast item from the database using its [compositeKey].
     */
    suspend fun removeFavoriteById(compositeKey: String) {
        favoritesDao.removeFavoriteById(compositeKey)
    }
}