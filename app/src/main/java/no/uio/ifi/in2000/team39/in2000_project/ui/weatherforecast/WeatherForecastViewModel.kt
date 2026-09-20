package no.uio.ifi.in2000.team39.in2000_project.ui.weatherforecast

import android.util.Log
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team39.in2000_project.data.grib.GRIB2RepositoryImplementation
import no.uio.ifi.in2000.team39.in2000_project.data.weather.WeatherForecastRepositoryImplementation
import no.uio.ifi.in2000.team39.in2000_project.model.favorite.FavoriteForecastItemEntity
import no.uio.ifi.in2000.team39.in2000_project.model.grib.GridInfo
import no.uio.ifi.in2000.team39.in2000_project.model.weather.TimeSeries
import no.uio.ifi.in2000.team39.in2000_project.model.weather.WeatherFeature
import no.uio.ifi.in2000.team39.in2000_project.ui.favorite.FavoritesViewModel
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Holds the UI state for the weather forecast screen.
 */
data class WeatherForecastUiState(
    val uiState: UIState = UIState.LOADING,
    val weatherFeature: WeatherFeature? = null,
    val timeSeries: List<TimeSeries> = emptyList(),
    val errorMessage: String? = null,
    val forecastResultsMap: Map<String, TimeSeriesEvaluationResult> = emptyMap(),
    val gridInfoMap: Map<String, GridInfo> = emptyMap(),
    val expandedItemIndices: Set<String> = emptySet(),
    val favoriteItemIndices: Set<String> = emptySet(),
    val mostRecentGridInfo: GridInfo = GridInfo(null, null),
    val isFiltered: Boolean = false,
    val filteredTimeSeries: List<TimeSeries> = emptyList(),
)

/**
 * Represents the evaluation results of various weather conditions for a single forecast instance.
 * Each property in this class reflects the status of a specific meteorological factor,
 * categorized by a status color which indicates the severity or normalcy of the condition.
 */
data class TimeSeriesEvaluationResult(
    val groundWindSpeedStatus: StatusColor,
    val groundWindGustSpeedStatus: StatusColor,
    val windShearStatus: StatusColor,
    val fogStatus: StatusColor,
    val precipitationStatus: StatusColor,
    val humidityStatus: StatusColor,
    val cloudCoverageHighStatus: StatusColor,
    val cloudCoverageLowStatus: StatusColor,
    val cloudCoverageMediumStatus: StatusColor,
    val maxAirWindSpeed: StatusColor,
    val summary: StatusColor,
)

/**
 * Enumerates the colors used to indicate the status of weather conditions.
 * Each color is associated with an icon for visual representation in the UI.
 */
enum class StatusColor(val color: Color, val icon: ImageVector) {
    GREEN(Color(0xFF019E06), Icons.Filled.CheckCircle),
    YELLOW(Color(0xFFE09C49), Icons.Filled.Warning),
    GRAY(Color(0xFF9C9C9C), Icons.Filled.Info),
    RED(Color(0xFFB90000), Icons.Filled.Error),
}

/**
 * Defines the possible states of the UI in response to data loading processes.
 * This helps in managing the UI state transitions clearly throughout the application.
 */
enum class UIState {
    LOADING, SUCCESS, ERROR
}

/**
 * Manages the caching and updating of weather and grid information for a given set of coordinates and altitude.
 */
class WeatherForecastViewModel : ViewModel() {
    private val weatherForecastRepository = WeatherForecastRepositoryImplementation()
    private val gRIB2Repository = GRIB2RepositoryImplementation()

    private val weatherDetailsCache = mutableMapOf<String, WeatherFeature?>()
    private val gridInfoCache = mutableMapOf<String, Map<String, GridInfo>>()

    private val _weatherForecastUiState = MutableStateFlow(WeatherForecastUiState())
    val weatherForecastUiState: StateFlow<WeatherForecastUiState> =
        _weatherForecastUiState.asStateFlow()

    /**
     * Initializes weather data fetching for specified [lat], [lon], and [altitude].
     * Sets the UI state accordingly and handles potential invalid coordinate inputs.
     */
    fun initialize(
        lat: String, lon: String, altitude: String
    ) {
        if (_weatherForecastUiState.value.uiState != UIState.LOADING) return

        viewModelScope.launch(Dispatchers.IO) {
            val latArg = lat.toFloatOrNull()
            val lonArg = lon.toFloatOrNull()
            val altitudeArg = altitude.toFloatOrNull()

            if (latArg == null || lonArg == null || altitudeArg == null) {
                _weatherForecastUiState.update {
                    it.copy(
                        uiState = UIState.ERROR,
                        errorMessage = "Invalid coordinates or altitude provided."
                    )
                }
                return@launch
            }

            try {
                val weatherDetails = fetchWeatherDetails(latArg, lonArg)
                val gridInfo = fetchGridInfo(latArg, lonArg, altitudeArg)

                updateWeatherDetailsState(weatherDetails)
                updateGridInfoState(gridInfo)
                populateEvalResult()

                _weatherForecastUiState.update { it.copy(uiState = UIState.SUCCESS) }
            } catch (e: Exception) {
                _weatherForecastUiState.update {
                    it.copy(
                        uiState = UIState.ERROR,
                        errorMessage = e.localizedMessage
                            ?: "An error occurred while fetching data."
                    )
                }
            }
        }
    }

    /**
     * Retrieves weather details from the repository based on [lat] and [lon].
     * If the data is already cached, it returns the cached data; otherwise, it fetches and updates the cache.
     */
    private suspend fun fetchWeatherDetails(
        lat: Float, lon: Float
    ): WeatherFeature {
        val cacheKey = "$lat,$lon"
        weatherDetailsCache[cacheKey]?.let { return it }

        val result = weatherForecastRepository.getWeatherDetails(lat, lon)
        return if (result.isSuccess) {
            result.getOrNull()?.also {
                weatherDetailsCache[cacheKey] = it
            } ?: throw IllegalStateException("No weather data received.")
        } else {
            throw result.exceptionOrNull()
                ?: IllegalStateException("Unknown error fetching weather details.")
        }
    }


    /**
     * Retrieves grid information from the repository based on [lat], [lon], and [altitude].
     * If the data is already cached, it returns the cached data; otherwise, it fetches and updates the cache.
     */
    private suspend fun fetchGridInfo(
        lat: Float, lon: Float, altitude: Float
    ): Map<String, GridInfo> {
        val cacheKey = "$lat,$lon,$altitude"
        gridInfoCache[cacheKey]?.let { return it }

        return gRIB2Repository.getGrib2Info(lat.toDouble(), lon.toDouble(), altitude.toDouble())
            .also {
                gridInfoCache[cacheKey] = it
            }
    }

    /**
     * Updates the UI state with newly fetched or cached weather details.
     */
    private fun updateWeatherDetailsState(weatherFeature: WeatherFeature?) {
        _weatherForecastUiState.update { currentState ->
            currentState.copy(
                weatherFeature = weatherFeature,
                timeSeries = weatherFeature?.properties?.timeseries ?: emptyList()
            )
        }
    }

    /**
     * Updates the UI state with newly fetched or cached grid information.
     */
    private fun updateGridInfoState(gridInfoMap: Map<String, GridInfo>) {
        _weatherForecastUiState.update { currentState ->
            currentState.copy(gridInfoMap = gridInfoMap)
        }
    }

    /**
     * Evaluates the fetched weather details against predefined thresholds to determine their status colors.
     */
    private fun populateEvalResult() {
        viewModelScope.launch(Dispatchers.Default) {
            val currentState = _weatherForecastUiState.value

            val evalResults = currentState.timeSeries.map { timeSeriesItem ->
                if (weatherForecastUiState.value.gridInfoMap.containsKey(timeSeriesItem.time)) {
                    updateMostRecentGridInfo(weatherForecastUiState.value.gridInfoMap[timeSeriesItem.time])
                }

                async {
                    val gridInfo = currentState.gridInfoMap[timeSeriesItem.time]
                        ?: weatherForecastUiState.value.mostRecentGridInfo
                    timeSeriesItem.time to evaluateForecast(
                        timeSeries = timeSeriesItem, gridInfo = gridInfo
                    )
                }
            }.awaitAll().toMap()

            _weatherForecastUiState.update { it.copy(forecastResultsMap = evalResults) }
        }
    }

    /**
     * Updates the UI state with the most recent [gridInfo], if available.
     */
    private fun updateMostRecentGridInfo(gridInfo: GridInfo?) {
        if (gridInfo == null) {
            return
        }

        _weatherForecastUiState.update { currentState ->
            currentState.copy(mostRecentGridInfo = gridInfo)
        }
    }

    /**
     * Constants defining thresholds for evaluating various weather conditions. These thresholds help in determining
     * the color status (RED, YELLOW, GREEN) for different aspects of the weather forecast based on defined limits.
     */
    companion object {
        const val GREEN_TRESHOLD_FACTOR = 0.6
        const val MAX_GROUND_WIND_SPEED = 8.6
        const val MAX_AIR_WIND_SPEED = 17.2
        const val MAX_SHEAR_WIND = 24.5
        const val MAX_CLOUD_COVERAGE_HIGH = 15.0
        const val MAX_CLOUD_COVERAGE_MEDIUM = 15.0
        const val MAX_CLOUD_COVERAGE_LOW = 5.0
        const val MAX_FOG_COVERAGE = 0.0
        const val MAX_PRECIPITATION_AMOUNT = 0.0
        const val MAX_HUMIDITY = 75.0
    }

    /**
     * Uses defined thresholds to evaluate the status of various weather conditions and assigns colors accordingly.
     */
    private fun evaluateForecast(
        timeSeries: TimeSeries, gridInfo: GridInfo
    ): TimeSeriesEvaluationResult {
        fun evaluateStatus(
            parameterValue: Double?, threshold: Double
        ): StatusColor {
            return when {
                parameterValue == null -> StatusColor.GRAY
                parameterValue > threshold -> StatusColor.RED
                parameterValue > 0 && parameterValue > threshold * GREEN_TRESHOLD_FACTOR -> StatusColor.YELLOW
                else -> StatusColor.GREEN
            }
        }

        // Evaluates the conditions based on predefined thresholds
        val groundWindSpeedStatus =
            evaluateStatus(timeSeries.data.instant.details.windSpeed, MAX_GROUND_WIND_SPEED)
        val groundWindGustSpeedStatus =
            evaluateStatus(timeSeries.data.instant.details.windSpeedOfGust, MAX_GROUND_WIND_SPEED)
        val windShearStatus = evaluateStatus(gridInfo.maxWindShear, MAX_SHEAR_WIND)
        val maxAirWindSpeed = evaluateStatus(gridInfo.maxWind, MAX_AIR_WIND_SPEED)
        val cloudCoverageHighStatus = evaluateStatus(
            timeSeries.data.instant.details.cloudAreaFractionHigh, MAX_CLOUD_COVERAGE_HIGH
        )
        val cloudCoverageMediumStatus = evaluateStatus(
            timeSeries.data.instant.details.cloudAreaFractionMedium, MAX_CLOUD_COVERAGE_MEDIUM
        )
        val cloudCoverageLowStatus = evaluateStatus(
            timeSeries.data.instant.details.cloudAreaFractionLow, MAX_CLOUD_COVERAGE_LOW
        )
        val fogStatus =
            evaluateStatus(timeSeries.data.instant.details.fogAreaFraction, MAX_FOG_COVERAGE)

        val precipitationStatus = evaluateStatus(
            timeSeries.data.next1Hours?.details?.precipitationAmount, MAX_PRECIPITATION_AMOUNT
        )
        val humidityStatus =
            evaluateStatus(timeSeries.data.instant.details.relativeHumidity, MAX_HUMIDITY)

        val summary = findWorstColor(
            listOf(
                groundWindSpeedStatus,
                groundWindGustSpeedStatus,
                windShearStatus,
                maxAirWindSpeed,
                cloudCoverageHighStatus,
                cloudCoverageMediumStatus,
                cloudCoverageLowStatus,
                fogStatus,
                precipitationStatus,
                humidityStatus
            )
        )

        return TimeSeriesEvaluationResult(
            groundWindSpeedStatus = groundWindSpeedStatus,
            groundWindGustSpeedStatus = groundWindGustSpeedStatus,
            windShearStatus = windShearStatus,
            maxAirWindSpeed = maxAirWindSpeed,
            cloudCoverageHighStatus = cloudCoverageHighStatus,
            cloudCoverageMediumStatus = cloudCoverageMediumStatus,
            cloudCoverageLowStatus = cloudCoverageLowStatus,
            fogStatus = fogStatus,
            precipitationStatus = precipitationStatus,
            humidityStatus = humidityStatus,
            summary = summary
        )
    }

    /**
     * Identifies the most severe status color from a list of colors, indicating the worst condition present in the weather forecast.
     */
    private fun findWorstColor(statusColors: List<StatusColor>): StatusColor {
        return statusColors.maxByOrNull { it.ordinal } ?: StatusColor.GREEN
    }

    /**
     * Adds a forecast to the favorites list within the application, handling the creation of a favorite forecast entity
     * with comprehensive weather parameters and metadata. The addition is performed asynchronously within `viewModelScope`.
     * This method reflects the new addition by updating the UI accordingly.
     */
    fun addFavoriteForecast(
        time: String,
        lat: Double,
        lon: Double,
        altitude: Double,
        groundWindSpeed: Double?,
        groundWindSpeedUnit: String?,
        groundWindSpeedStatus: String?,
        gustWindSpeed: Double?,
        gustWindSpeedUnit: String?,
        gustWindSpeedStatus: String?,
        maxWindShear: Double?,
        maxWindShearUnit: String?,
        maxWindShearStatus: String?,
        maxAirWindSpeed: Double?,
        maxAirWindSpeedUnit: String?,
        maxAirWindSpeedStatus: String?,
        cloudCoverageHigh: Double?,
        cloudCoverageHighUnit: String?,
        cloudCoverageHighStatus: String?,
        cloudCoverageMedium: Double?,
        cloudCoverageMediumUnit: String?,
        cloudCoverageMediumStatus: String?,
        cloudCoverageLow: Double?,
        cloudCoverageLowUnit: String?,
        cloudCoverageLowStatus: String?,
        fogAreaFraction: Double?,
        fogAreaFractionUnit: String?,
        fogAreaFractionStatus: String?,
        precipitationAmount: Double?,
        precipitationAmountUnit: String?,
        precipitationAmountStatus: String?,
        relativeHumidity: Double?,
        relativeHumidityUnit: String?,
        relativeHumidityStatus: String?,
        summary: String?,
        favoritesViewModel: FavoritesViewModel,
    ) {
        viewModelScope.launch {
            val compositeKey = FavoriteForecastItemEntity.createCompositeKey(
                time = time, lon = lon, lat = lat, altitude = altitude
            )

            val favorite = FavoriteForecastItemEntity(
                compositeKey = compositeKey,
                time = time,
                lon = lon,
                lat = lat,
                altitude = altitude,
                groundWindSpeed = groundWindSpeed,
                groundWindSpeedUnit = groundWindSpeedUnit,
                groundWindSpeedStatus = groundWindSpeedStatus,
                gustWindSpeed = gustWindSpeed,
                gustWindSpeedUnit = gustWindSpeedUnit,
                gustWindSpeedStatus = gustWindSpeedStatus,
                maxWindShear = maxWindShear,
                maxWindShearUnit = maxWindShearUnit,
                maxWindShearStatus = maxWindShearStatus,
                maxAirWindSpeed = maxAirWindSpeed,
                maxAirWindSpeedUnit = maxAirWindSpeedUnit,
                maxAirWindSpeedStatus = maxAirWindSpeedStatus,
                cloudCoverageHigh = cloudCoverageHigh,
                cloudCoverageHighUnit = cloudCoverageHighUnit,
                cloudCoverageHighStatus = cloudCoverageHighStatus,
                cloudCoverageMedium = cloudCoverageMedium,
                cloudCoverageMediumUnit = cloudCoverageMediumUnit,
                cloudCoverageMediumStatus = cloudCoverageMediumStatus,
                cloudCoverageLow = cloudCoverageLow,
                cloudCoverageLowUnit = cloudCoverageLowUnit,
                cloudCoverageLowStatus = cloudCoverageLowStatus,
                fogAreaFraction = fogAreaFraction,
                fogAreaFractionUnit = fogAreaFractionUnit,
                fogAreaFractionStatus = fogAreaFractionStatus,
                precipitationAmount = precipitationAmount,
                precipitationAmountUnit = precipitationAmountUnit,
                precipitationAmountStatus = precipitationAmountStatus,
                relativeHumidity = relativeHumidity,
                relativeHumidityUnit = relativeHumidityUnit,
                relativeHumidityStatus = relativeHumidityStatus,
                summary = summary,
            )

            favoritesViewModel.addFavorite(favorite)
            toggleItemFavorited(time)
        }
    }

    /**
     * Removes a forecast from the favorites list based on specific geographic and temporal identifiers.
     * This function constructs a composite key using [time], [lat], [lon], and [altitude] to uniquely identify a favorite
     * forecast. It then delegates the removal of this forecast to the [favoritesViewModel], which manages the state of
     * favorite forecasts across the application.
     *
     * After successfully removing the forecast, it updates the UI to reflect this change by toggling the favorited
     * status of the item associated with the provided [time].
     */
    fun removeFavoriteForecast(
        time: String,
        favoritesViewModel: FavoritesViewModel,
        lat: Double,
        lon: Double,
        altitude: Double,
    ) {
        viewModelScope.launch {
            val compositeKey = FavoriteForecastItemEntity.createCompositeKey(
                time = time, lon = lon, lat = lat, altitude = altitude
            )

            favoritesViewModel.removeFavorite(compositeKey)
            toggleItemFavorited(time)
        }
    }

    /**
     * Toggles the expanded state of an item identified by its [index] in the forecast list.
     * This method is used to expand or collapse forecast details in the UI.
     */
    fun toggleItemExpanded(index: String) {
        val currentState = weatherForecastUiState.value
        val newExpandedIndices = currentState.expandedItemIndices.toMutableSet().apply {
            if (contains(index)) remove(index) else add(index)
        }
        _weatherForecastUiState.value = currentState.copy(expandedItemIndices = newExpandedIndices)
    }

    /**
     * Toggles the favorited state of an item identified by its [index] in the forecast list.
     * This method is used to mark or unmark a forecast as a favorite in the UI, reflecting the user's preferences.
     */
    private fun toggleItemFavorited(index: String) {
        val currentState = weatherForecastUiState.value
        val newFavoriteIndices = currentState.favoriteItemIndices.toMutableSet().apply {
            if (contains(index)) remove(index)
            else add(index)
        }
        _weatherForecastUiState.value = currentState.copy(favoriteItemIndices = newFavoriteIndices)
    }

    /**
     * Filters the list of `TimeSeries` to include only those with a non-RED summary status.
     * This method first checks if the filtered list is already populated to avoid redundant computations.
     * If not populated, it fetches the filtered list via `getTimeSeriesWithNonRedSummary`.
     * It then toggles the filter status to reflect that the list is now filtered.
     */
    fun filterNonRed() {
        val currentState = weatherForecastUiState.value

        if (currentState.filteredTimeSeries.isEmpty()) {
            _weatherForecastUiState.value =
                currentState.copy(filteredTimeSeries = getTimeSeriesWithNonRedSummary())
        }

        toggleFilterNonRed()
    }

    /**
     * Retrieves the appropriate list of `TimeSeries` based on the current filter state.
     * Returns a filtered list if the `isFiltered` flag is true, otherwise returns the full list.
     */
    fun getTimeSeries(): List<TimeSeries> {
        val currentState = weatherForecastUiState.value

        return if (currentState.isFiltered) {
            currentState.filteredTimeSeries
        } else {
            currentState.timeSeries
        }
    }

    fun getTimeSeriesGroupedByDate(timeSeries: List<TimeSeries>): Map<String, List<TimeSeries>> {
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateTimeFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

        return timeSeries.groupBy { timeSeriesItem ->
            val date = try {
                dateTimeFormatter.parse(timeSeriesItem.time)
            } catch (e: ParseException) {
                null
            }
            date?.let { dateFormatter.format(it) } ?: "Invalid Date"
        }
    }

    /**
     * Toggles the `isFiltered` state of the UI state. This method is used internally
     * to switch between filtered and unfiltered views of the `TimeSeries` data.
     */
    private fun toggleFilterNonRed() {
        val currentState = weatherForecastUiState.value
        _weatherForecastUiState.value = currentState.copy(isFiltered = !currentState.isFiltered)
    }

    /**
     * Generates a list of `TimeSeries` that excludes those with a RED summary status,
     * based on evaluations in the `forecastResultsMap`.
     */
    private fun getTimeSeriesWithNonRedSummary(): List<TimeSeries> {
        val currentState = _weatherForecastUiState.value
        val filteredKeys = currentState.forecastResultsMap.filter {
            it.value.summary != StatusColor.RED
        }.keys

        return currentState.timeSeries.filter { timeSeries ->
            filteredKeys.contains(timeSeries.time)
        }
    }
}
