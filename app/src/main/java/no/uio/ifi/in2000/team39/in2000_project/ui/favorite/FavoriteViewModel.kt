package no.uio.ifi.in2000.team39.in2000_project.ui.favorite

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team39.in2000_project.data.favorite.FavoritesRepository
import no.uio.ifi.in2000.team39.in2000_project.model.favorite.FavoriteForecastItemEntity

/**
 * Holds the state for the favorites feature in the application, tracking which items are expanded, favorited,
 * and listing all favorite forecast items.
 */
data class FavoriteForecastUiState(
    val expandedItemIndices: Set<String> = emptySet(),
    val favoritedItemIndices: Set<String> = emptySet(),
    val favorites: Map<String, FavoriteForecastItemEntity> = emptyMap()
)

/**
 * Manages data and interactions related to favorite weather forecasts.
 * Utilizes [FavoritesRepository] to handle data persistence and fetch operations, keeping UI state responsive and up-to-date.
 */
class FavoritesViewModel(application: Application) : AndroidViewModel(application) {
    private val favoritesRepository = FavoritesRepository(application)
    private val _favoriteForecastUiState = MutableStateFlow(FavoriteForecastUiState())
    val favoriteForecastUiState: StateFlow<FavoriteForecastUiState> =
        _favoriteForecastUiState.asStateFlow()
    private var isInitialized = false

    init {
        if (!isInitialized) {
            isInitialized = true
            loadFavorites()
        }
    }

    /**
     * Fetches favorite items from the repository and updates the UI state.
     * Ensures that the UI reflects the latest data from the local database.
     */
    private fun loadFavorites() {
        viewModelScope.launch(Dispatchers.IO) {
            val favoritesMap = favoritesRepository.getAllFavorites()
            _favoriteForecastUiState.value = FavoriteForecastUiState(
                favorites = favoritesMap,
                favoritedItemIndices = favoritesMap.keys
            )
        }
    }

    /**
     * Inserts a new favorite into the repository.
     * Adds a [favorite] item to persistent storage.
     */
    fun addFavorite(favorite: FavoriteForecastItemEntity) = viewModelScope.launch(Dispatchers.IO) {
        favoritesRepository.insertFavorite(favorite)
    }

    /**
     * Removes a favorite from the repository based on its identifier.
     * Deletes the favorite identified by [compositeKey] from persistent storage.
     */
    fun removeFavorite(compositeKey: String) = viewModelScope.launch(Dispatchers.IO) {
        favoritesRepository.removeFavoriteById(compositeKey)
    }

    /**
     * Toggles the expanded state of an item in the favorites list.
     * Expands or collapses the item identified by [compositeKey], updating the UI state accordingly.
     */
    fun toggleItemExpanded(compositeKey: String) {
        _favoriteForecastUiState.update { currentState ->
            val newExpandedIndices = currentState.expandedItemIndices.toMutableSet().apply {
                if (contains(compositeKey)) remove(compositeKey) else add(compositeKey)
            }
            currentState.copy(expandedItemIndices = newExpandedIndices)
        }
    }

    /**
     * Toggles the favorited state of an item in the favorites list.
     * Either marks the item identified by [compositeKey] as a favorite or removes it from the favorites, updating both UI and persistent states.
     */
    fun toggleItemFavorited(compositeKey: String) {
        viewModelScope.launch {
            val currentState = favoriteForecastUiState.value
            val isFavorited = compositeKey in currentState.favoritedItemIndices

            if (isFavorited) {
                removeFavorite(compositeKey)
            } else {
                currentState.favorites[compositeKey]?.let { addFavorite(it) }
            }

            _favoriteForecastUiState.update { state ->
                val updatedFavorites = if (isFavorited) {
                    state.favoritedItemIndices - compositeKey
                } else {
                    state.favoritedItemIndices + compositeKey
                }
                state.copy(favoritedItemIndices = updatedFavorites)
            }
        }
    }
}
