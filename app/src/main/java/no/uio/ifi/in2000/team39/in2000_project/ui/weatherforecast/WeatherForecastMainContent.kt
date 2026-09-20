package no.uio.ifi.in2000.team39.in2000_project.ui.weatherforecast

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import no.uio.ifi.in2000.team39.in2000_project.R
import no.uio.ifi.in2000.team39.in2000_project.model.grib.GridInfo
import no.uio.ifi.in2000.team39.in2000_project.model.weather.Details
import no.uio.ifi.in2000.team39.in2000_project.model.weather.TimeSeries
import no.uio.ifi.in2000.team39.in2000_project.model.weather.WeatherUnits
import no.uio.ifi.in2000.team39.in2000_project.ui.favorite.FavoritesViewModel
import no.uio.ifi.in2000.team39.in2000_project.ui.shared.date.DateFormatter
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.SortedMap

/**
 * Displays the main content for the weather forecast, organizing weather data into a scrollable
 * list of daily forecasts. This component groups weather data by day and uses a detailed card for
 * each day to present the weather information. [evaluationResults] provides evaluation results
 * for each time series, which may affect visual indicators of weather severity or noticeability.
 * [gridInfoMap] contains metadata for each time series, such as grid-specific details that enhance
 * the forecast's accuracy. [weatherForecastViewModel] manages the state and processing of
 * weather data for UI rendering. [favoritesViewModel] handles the favorite selections and
 * interactions within the weather forecast. [altitude] represents the altitude at which the
 * weather data is evaluated, affecting certain weather calculations. [timeSeries] is a list of
 * weather data entries sorted by time, which the function groups by day for display.
 */
@Composable
fun WeatherForecastMainContent(
    evaluationResults: Map<String, TimeSeriesEvaluationResult>,
    gridInfoMap: Map<String, GridInfo>,
    weatherForecastViewModel: WeatherForecastViewModel,
    favoritesViewModel: FavoritesViewModel = viewModel(),
    altitude: Double,
    timeSeries: List<TimeSeries>,
) {
    val groupedTimeseries = weatherForecastViewModel.getTimeSeriesGroupedByDate(timeSeries)

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        if (timeSeries.isEmpty()) {
            item { NoWeatherDataAvailable() }
        } else {
            val sortedGridInfoMap = gridInfoMap.toSortedMap()

            groupedTimeseries.forEach { (day, timeseriesList) ->
                item {
                    DayForecastCard(
                        day = day,
                        timeseriesList = timeseriesList,
                        weatherForecastViewModel = weatherForecastViewModel,
                        favoritesViewModel = favoritesViewModel,
                        evaluationResults = evaluationResults,
                        gridInfoMap = sortedGridInfoMap,
                        altitude = altitude,
                    )
                }
            }
        }
    }
}

/**
 * Displays a message indicating that no weather data is available.
 * This function creates a full-screen box with centered text, providing
 * a user-friendly message when there is no weather data to display.
 */
@Composable
fun NoWeatherDataAvailable() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "No weather data available")
    }
}

/**
 * Displays a single day's weather forecast within an outlined card, allowing for expansion to show
 * more details. This component handles both display and user interactions like expanding to see
 * detailed forecasts and toggling favorite status for specific time points. [day] specifies the
 * date for the weather data being displayed. [timeseriesList] lists all time series data points
 * that belong to the given day. [weatherForecastViewModel] provides the necessary functions and
 * state handling for weather-related operations. [favoritesViewModel] manages favorite weather points
 * and interactions like adding or removing favorites. [evaluationResults] contains evaluation details
 * for each time series, used to determine icons and color codes based on weather conditions.
 * [gridInfoMap] provides additional data points like wind shear and maximum wind speeds which are
 * not part of the basic time series data. [altitude] affects certain weather calculations and is
 * used for displaying relevant weather data at specific elevations.
 */
@Composable
fun DayForecastCard(
    day: String,
    timeseriesList: List<TimeSeries>,
    weatherForecastViewModel: WeatherForecastViewModel,
    favoritesViewModel: FavoritesViewModel,
    evaluationResults: Map<String, TimeSeriesEvaluationResult>,
    gridInfoMap: SortedMap<String, GridInfo>,
    altitude: Double,
) {
    val weatherForecastUiState =
        weatherForecastViewModel.weatherForecastUiState.collectAsState().value
    val isExpanded = weatherForecastUiState.expandedItemIndices.contains(day)
    val formattedDay = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(
        SimpleDateFormat(
            "yyyy-MM-dd", Locale.getDefault()
        ).parse(day)!!
    )
    val weatherFeature = weatherForecastUiState.weatherFeature

    var mostRecentGridInfo = gridInfoMap[gridInfoMap.firstKey()]
    val mutableGridInfoMap = gridInfoMap.toMutableMap()

    timeseriesList.forEach {
        if (gridInfoMap.containsKey(it.time)) {
            mostRecentGridInfo = gridInfoMap[it.time]
        }

        mutableGridInfoMap[it.time] = mostRecentGridInfo
    }

    OutlinedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.outlinedCardElevation(10.dp),
        border = BorderStroke(1.dp, Color.Black.copy(0.25f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .widthIn(max = 50.dp),
        onClick = { weatherForecastViewModel.toggleItemExpanded(day) }
    ) {
        Text(
            text = formattedDay,
            style = MaterialTheme.typography.titleLarge,
            fontSize = 18.sp,
            modifier = Modifier.padding(vertical = 25.dp, horizontal = 10.dp)
        )

        if (isExpanded) {
            timeseriesList.forEach { timeserie ->
                val isFavorite = weatherForecastUiState.favoriteItemIndices.contains(timeserie.time)

                weatherFeature?.properties?.meta?.let {
                    val instantDetails = timeserie.data.instant.details
                    val units = weatherFeature.properties.meta.units
                    val evaluationResult = evaluationResults[timeserie.time]
                    val gridInfo = mutableGridInfoMap[timeserie.time]

                    WeatherForecastItem(
                        timeserie = timeserie,
                        isFavorite = isFavorite,
                        instantDetails = instantDetails,
                        onFavoriteClicked = {
                            if (isFavorite) {
                                weatherForecastViewModel.removeFavoriteForecast(
                                    time = timeserie.time,
                                    lat = weatherFeature.geometry.coordinates[1],
                                    lon = weatherFeature.geometry.coordinates[0],
                                    altitude = altitude,
                                    favoritesViewModel = favoritesViewModel
                                )
                            } else {
                                weatherForecastViewModel.addFavoriteForecast(
                                    favoritesViewModel = favoritesViewModel,
                                    time = timeserie.time,
                                    lat = weatherFeature.geometry.coordinates[1],
                                    lon = weatherFeature.geometry.coordinates[0],
                                    altitude = altitude,
                                    groundWindSpeed = instantDetails.windSpeed,
                                    groundWindSpeedUnit = units.windSpeedUnit,
                                    groundWindSpeedStatus = evaluationResult?.groundWindSpeedStatus?.name,
                                    gustWindSpeed = instantDetails.windSpeedOfGust,
                                    gustWindSpeedUnit = units.windSpeedOfGustUnit,
                                    gustWindSpeedStatus = evaluationResult?.groundWindGustSpeedStatus?.name,
                                    maxWindShear = gridInfo?.maxWindShear
                                        ?: weatherForecastViewModel.weatherForecastUiState.value.mostRecentGridInfo.maxWindShear,
                                    maxWindShearUnit = "m/s",
                                    maxWindShearStatus = evaluationResult?.windShearStatus?.name,
                                    maxAirWindSpeed = gridInfo?.maxWind
                                        ?: weatherForecastViewModel.weatherForecastUiState.value.mostRecentGridInfo.maxWind,
                                    maxAirWindSpeedUnit = "m/s",
                                    maxAirWindSpeedStatus = evaluationResult?.maxAirWindSpeed?.name,
                                    cloudCoverageHigh = instantDetails.cloudAreaFractionHigh,
                                    cloudCoverageHighUnit = units.cloudAreaFractionHighUnit,
                                    cloudCoverageHighStatus = evaluationResult?.cloudCoverageHighStatus?.name,
                                    cloudCoverageMedium = instantDetails.cloudAreaFractionMedium,
                                    cloudCoverageMediumUnit = units.cloudAreaFractionMediumUnit,
                                    cloudCoverageMediumStatus = evaluationResult?.cloudCoverageMediumStatus?.name,
                                    cloudCoverageLow = instantDetails.cloudAreaFractionLow,
                                    cloudCoverageLowUnit = units.cloudAreaFractionLowUnit,
                                    cloudCoverageLowStatus = evaluationResult?.cloudCoverageLowStatus?.name,
                                    fogAreaFraction = instantDetails.fogAreaFraction,
                                    fogAreaFractionUnit = units.fogAreaFractionUnit,
                                    fogAreaFractionStatus = evaluationResult?.fogStatus?.name,
                                    precipitationAmount = timeserie.data.next1Hours?.details?.precipitationAmount,
                                    precipitationAmountUnit = units.precipitationAmountUnit,
                                    precipitationAmountStatus = evaluationResult?.precipitationStatus?.name,
                                    relativeHumidity = instantDetails.relativeHumidity,
                                    relativeHumidityUnit = units.relativeHumidityUnit,
                                    relativeHumidityStatus = evaluationResult?.humidityStatus?.name,
                                    summary = evaluationResult?.summary?.name,
                                )
                            }
                        },
                        evaluationResult = evaluationResult,
                        units = it.units,
                        gridInfo = gridInfo
                            ?: weatherForecastViewModel.weatherForecastUiState.value.mostRecentGridInfo,
                        isExpanded = weatherForecastUiState.expandedItemIndices.contains(timeserie.time),
                        onItemClicked = {
                            weatherForecastViewModel.toggleItemExpanded(timeserie.time)
                        },
                    )
                }
            }
        }
    }
}

/**
 * Displays a single weather forecast item within an outlined card. The card shows detailed weather
 * information, favorite status toggle, and allows for expansion to show additional weather details.
 * The [timeserie] represents a single time series data entry for weather. Evaluation results for the
 * specific time are contained in [evaluationResult], used to show status icons. The [units] hold
 * the measurement units for the displayed weather data, and [gridInfo] contains additional grid-based
 * information like wind speed and direction. The [isExpanded] parameter controls whether additional
 * details about the weather are displayed. The [isFavorite] parameter indicates whether the current
 * timeserie is marked as a favorite. Callback functions [onItemClicked] and [onFavoriteClicked] handle
 * item clicks and toggling the favorite status, respectively. Detailed weather information like
 * temperature, wind speed, and humidity is provided by [instantDetails].
 */
@Composable
fun WeatherForecastItem(
    timeserie: TimeSeries,
    evaluationResult: TimeSeriesEvaluationResult?,
    units: WeatherUnits,
    gridInfo: GridInfo,
    isExpanded: Boolean,
    isFavorite: Boolean,
    onItemClicked: () -> Unit,
    onFavoriteClicked: () -> Unit,
    instantDetails: Details,
) {
    val formattedDate = DateFormatter.hourFormat(timeserie.time)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 0.dp, bottom = 15.dp)
            .clickable { onItemClicked() },
    ) {
        OutlinedCard(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
            elevation = CardDefaults.outlinedCardElevation(10.dp),
            border = BorderStroke(1.dp, Color.Black.copy(0.25f)),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formattedDate ?: stringResource(id = R.string.unknown_date),
                    style = MaterialTheme.typography.titleMedium
                )

                IconButton(onClick = { onFavoriteClicked() }) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = if (isFavorite) stringResource(id = R.string.add_favorite) else stringResource(
                            id = R.string.remove_favorite
                        )
                    )
                }

                evaluationResult?.let { StatusIcon(statusColor = it.summary) }
            }

            WeatherDetailRow(
                label = stringResource(id = R.string.ground_wind_label),
                value = instantDetails.windSpeed,
                unit = units.windSpeedUnit,
                status = evaluationResult?.groundWindSpeedStatus
            )

            WeatherDetailRow(
                label = stringResource(id = R.string.humidity_amount),
                value = instantDetails.relativeHumidity,
                unit = units.relativeHumidityUnit,
                status = evaluationResult?.humidityStatus
            )

            if (isExpanded) {
                WeatherAdditionalDetails(
                    timeserie = timeserie,
                    instantDetails = instantDetails,
                    evaluationResult = evaluationResult,
                    units = units,
                    gridInfo = gridInfo
                )
            }

            Spacer(modifier = Modifier.padding(5.dp))
        }
    }
}

/**
 * Displays additional rows of detailed weather data for a specific timeserie within a weather
 * forecast. Each row corresponds to a different aspect of the weather, such as wind, cloud coverage,
 * or humidity. The [timeserie] represents the specific time series data for which these details apply.
 * Evaluation results relevant to the weather conditions of this time series are contained in
 * [evaluationResult], used to display status icons. The [units] hold the measurement units for each
 * weather data point, and [gridInfo] provides additional context-specific data like wind shear and
 * maximum wind speeds. Immediate details about the weather, such as wind speed and cloud coverage,
 * are held in [instantDetails].
 */
@Composable
fun WeatherAdditionalDetails(
    timeserie: TimeSeries,
    evaluationResult: TimeSeriesEvaluationResult?,
    units: WeatherUnits,
    gridInfo: GridInfo,
    instantDetails: Details,
) {
    WeatherDetailRow(
        label = stringResource(id = R.string.gust_wind_label),
        value = instantDetails.windSpeedOfGust,
        unit = units.windSpeedOfGustUnit,
        status = evaluationResult?.groundWindGustSpeedStatus
    )
    WeatherDetailRow(
        label = stringResource(id = R.string.shear_wind_label),
        value = gridInfo.maxWindShear,
        unit = stringResource(id = R.string.shear_unit),
        status = evaluationResult?.windShearStatus
    )
    WeatherDetailRow(
        label = stringResource(id = R.string.max_air_wind_label),
        value = gridInfo.maxWind,
        unit = stringResource(id = R.string.max_air_wind_unit),
        status = evaluationResult?.maxAirWindSpeed
    )
    WeatherDetailRow(
        label = stringResource(id = R.string.cloud_coverage_high_label),
        value = instantDetails.cloudAreaFractionHigh,
        unit = units.cloudAreaFractionHighUnit,
        status = evaluationResult?.cloudCoverageHighStatus
    )
    WeatherDetailRow(
        label = stringResource(id = R.string.cloud_coverage_medium_label),
        value = instantDetails.cloudAreaFractionMedium,
        unit = units.cloudAreaFractionMediumUnit,
        status = evaluationResult?.cloudCoverageMediumStatus
    )
    WeatherDetailRow(
        label = stringResource(id = R.string.cloud_coverage_low_label),
        value = instantDetails.cloudAreaFractionLow,
        unit = units.cloudAreaFractionLowUnit,
        status = evaluationResult?.cloudCoverageLowStatus
    )
    WeatherDetailRow(
        label = stringResource(id = R.string.fog_area_fraction),
        value = instantDetails.fogAreaFraction,
        unit = units.fogAreaFractionUnit,
        status = evaluationResult?.fogStatus
    )
    WeatherDetailRow(
        label = stringResource(id = R.string.precipitation_amount),
        value = timeserie.data.next1Hours?.details?.precipitationAmount,
        unit = units.precipitationAmountUnit,
        status = evaluationResult?.precipitationStatus
    )
}

/**
 * Renders a single row in the weather forecast details screen, showing weather information like
 * wind speed or cloud coverage. It displays the value with the unit and might include an icon
 * indicating the status of the weather attribute. The [label] describes the weather attribute. The
 * numerical value of the weather attribute is given by [value]; if null, "Not Available" is displayed.
 * The measurement unit for the value is provided by [unit], and the status color associated with the
 * attribute, used to render an icon, is provided by [status].
 */
@Composable
fun WeatherDetailRow(
    label: String,
    value: Double?,
    unit: String?,
    status: StatusColor?,
) {
    val notAvailable = stringResource(id = R.string.not_available)

    val displayValue = if (value == null) {
        notAvailable
    } else {
        stringResource(id = R.string.unit_format, value.format(), unit ?: notAvailable).trimEnd()
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp, horizontal = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = stringResource(id = R.string.detail_label_template, label, displayValue))

        status?.let {
            Icon(
                imageVector = it.icon, contentDescription = stringResource(
                    id = R.string.detail_content_description_template, label
                ), tint = it.color, modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}

/**
 * Displays an icon representing a weather condition's status. The icon's color changes based on the
 * weather condition's severity or status. The [statusColor] contains the icon and its associated
 * color to visually represent the weather status.
 */
@Composable
fun StatusIcon(statusColor: StatusColor) {
    Icon(
        imageVector = statusColor.icon,
        contentDescription = stringResource(id = R.string.status_icon_description),
        tint = statusColor.color
    )
}

fun Double.format(digits: Int = 2): String {
    return BigDecimal(this).setScale(digits, RoundingMode.HALF_UP).toString()
}