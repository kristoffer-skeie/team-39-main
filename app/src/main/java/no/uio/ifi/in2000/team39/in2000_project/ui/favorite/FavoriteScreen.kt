package no.uio.ifi.in2000.team39.in2000_project.ui.favorite

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import no.uio.ifi.in2000.team39.in2000_project.R
import no.uio.ifi.in2000.team39.in2000_project.model.favorite.FavoriteForecastItemEntity
import no.uio.ifi.in2000.team39.in2000_project.ui.shared.components.BottomBar
import no.uio.ifi.in2000.team39.in2000_project.ui.shared.components.TopBar
import no.uio.ifi.in2000.team39.in2000_project.ui.shared.date.DateFormatter
import no.uio.ifi.in2000.team39.in2000_project.ui.weatherforecast.StatusColor
import no.uio.ifi.in2000.team39.in2000_project.ui.weatherforecast.format

/**
 * Displays the favorite screen with a list of favorite weather forecasts.
 * The screen includes a top bar for navigation, a content area displaying favorite weather items,
 * and a bottom bar for additional navigation options. Navigation actions are managed via [navController],
 * and state and interactions for favorite forecasts are handled by [favoritesViewModel].
 */
@Composable
fun FavoriteScreen(
    navController: NavController,
    favoritesViewModel: FavoritesViewModel = viewModel(),
) {
    val favoritesForecastUiState by favoritesViewModel.favoriteForecastUiState.collectAsState()
    val favorites = favoritesForecastUiState.favorites
    val settingsRoute = stringResource(id = R.string.setting_screen_route)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        topBar = {
            TopBar(
                onBackClick = { navController.popBackStack() },
                onSettingsClick = { navController.navigate(settingsRoute) },
                title = stringResource(id = R.string.favorites_screen_title)
            )
        },
        bottomBar = {
            BottomBar(
                navController,
            )
        }
    ) { innerPadding ->
        FavoriteList(
            favorites, favoritesViewModel, favoritesForecastUiState, innerPadding
        )
    }
}

/**
 * Renders a list of weather forecast items [favorites].
 * Each item is interactable, allowing the user to expand for more details or update its favorite status.
 * The list is managed by [favoritesViewModel], which holds the state for favorited and expanded items in [favoritesForecastUiState].
 * Padding within the list is applied using [innerPadding].
 */
@Composable
private fun FavoriteList(
    favorites: Map<String, FavoriteForecastItemEntity>,
    favoritesViewModel: FavoritesViewModel,
    favoritesForecastUiState: FavoriteForecastUiState,
    innerPadding: PaddingValues,
) {
    LazyColumn(modifier = Modifier.padding(innerPadding)) {
        items(favorites.toList()) { (key, value) ->
            FavoriteItem(
                favorite = value,
                isFavorited = favoritesForecastUiState.favoritedItemIndices.contains(key),
                isExpanded = favoritesForecastUiState.expandedItemIndices.contains(key),
                onItemClicked = { favoritesViewModel.toggleItemExpanded(key) },
                onFavoriteChanged = { favoritesViewModel.toggleItemFavorited(key) }
            )
        }
    }
}


/**
 * Displays key details of the [favorite] forecast and provides interactive icons to expand for more information
 * or toggle its favorite status. The item can be clicked to expand and show more details. The favorite status
 * can also be toggled.
 *
 * The [favorite] item includes details such as the forecast time, location, and various weather parameters.
 * The [isFavorited] flag indicates whether the item is currently marked as a favorite, affecting the icon
 * displayed (showing either a filled or outlined heart). The [isExpanded] flag indicates whether the item's
 * details are currently expanded, showing additional weather parameters when true. The [onItemClicked]
 * lambda function is called when the item is clicked, handling expanding or collapsing the item. The
 * [onFavoriteChanged] lambda function is called when the favorite status is changed, toggling the item's
 * favorite status.
 */
@Composable
private fun FavoriteItem(
    favorite: FavoriteForecastItemEntity,
    isFavorited: Boolean,
    isExpanded: Boolean,
    onItemClicked: () -> Unit,
    onFavoriteChanged: () -> Unit,
) {
    val formattedDate = DateFormatter.fullDayFormat(favorite.time)

    Card(
        modifier = Modifier
            .padding(10.dp)
            .defaultMinSize(48.dp),
        elevation = CardDefaults.outlinedCardElevation(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        border = BorderStroke(1.dp, Color.Black.copy(0.25f)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable { onItemClicked() }
                .defaultMinSize(48.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Absolute.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formattedDate ?: stringResource(id = R.string.unknown_date),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.semantics { heading() }
                )

                IconButton(onClick = { onFavoriteChanged() }) {
                    Icon(
                        imageVector = if (isFavorited) {
                            Icons.Filled.Favorite
                        } else {
                            Icons.Filled.FavoriteBorder
                        },
                        contentDescription = if (isFavorited) {
                            stringResource(id = R.string.remove_favorite)
                        } else {
                            stringResource(id = R.string.add_favorite)
                        },
                        modifier = Modifier.defaultMinSize(48.dp)
                    )
                }

                val icon = when (favorite.summary) {
                    "YELLOW" -> StatusColor.YELLOW
                    "GREEN" -> StatusColor.GREEN
                    else -> StatusColor.RED
                }

                Icon(
                    imageVector = icon.icon,
                    contentDescription = "Summary Status",
                    tint = icon.color,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }

            val locationInfo = stringResource(
                id = R.string.location_info, favorite.lat, favorite.lon, favorite.altitude
            )

            Text(
                text = locationInfo, style = MaterialTheme.typography.titleSmall
            )

            DetailRow(
                label = stringResource(id = R.string.ground_wind_label),
                value = favorite.groundWindSpeed,
                unit = favorite.groundWindSpeedUnit,
                status = favorite.groundWindSpeedStatus
            )

            DetailRow(
                label = stringResource(id = R.string.humidity_amount),
                value = favorite.relativeHumidity,
                unit = favorite.relativeHumidityUnit,
                status = favorite.relativeHumidityStatus
            )

            Spacer(modifier = Modifier.height(4.dp))

            if (isExpanded) {
                DetailRows(favorite)
            }
        }
    }

}

/**
 * Displays a detailed row for each weather parameter such as wind speed or humidity. Each [label] describes
 * the parameter, and [value] shows the corresponding value with a [unit], formatted appropriately. The status
 * icon changes color based on the [status] of the parameter, providing visual feedback about conditions.
 */
@Composable
fun DetailRow(
    label: String,
    value: Double?,
    unit: String?,
    status: String?,
) {
    val displayValue = if (value == null) "N/A" else "${value.format()} $unit".trimEnd()

    var icon = StatusColor.RED

    if (status == "YELLOW") {
        icon = StatusColor.YELLOW
    } else if (status == "GREEN") {
        icon = StatusColor.GREEN
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label: $displayValue",
            modifier = Modifier.semantics { heading() }
        )

        Icon(
            imageVector = icon.icon,
            contentDescription = "$label Status",
            tint = icon.color,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}

/**
 * Displays detailed rows for various weather parameters of a [favorite] forecast item. Each detail row presents
 * specific data such as wind speed, cloud coverage, and more with respective units and status indicators.
 * This function calls `DetailRow` for each weather parameter, supplying it with labels, values, units, and statuses
 * to format and display each row consistently across different parameters.
 */
@Composable
fun DetailRows(
    favorite: FavoriteForecastItemEntity,
) {
    DetailRow(
        label = stringResource(id = R.string.gust_wind_label),
        value = favorite.gustWindSpeed,
        unit = favorite.gustWindSpeedUnit,
        status = favorite.gustWindSpeedStatus
    )
    DetailRow(
        label = stringResource(id = R.string.shear_wind_label),
        value = favorite.maxWindShear,
        unit = favorite.maxWindShearUnit,
        status = favorite.maxWindShearStatus
    )
    DetailRow(
        label = stringResource(id = R.string.max_air_wind_label),
        value = favorite.maxAirWindSpeed,
        unit = favorite.maxAirWindSpeedUnit,
        status = favorite.maxAirWindSpeedStatus
    )
    DetailRow(
        label = stringResource(id = R.string.cloud_coverage_high_label),
        value = favorite.cloudCoverageHigh,
        unit = favorite.cloudCoverageHighUnit,
        status = favorite.cloudCoverageHighStatus
    )
    DetailRow(
        label = stringResource(id = R.string.cloud_coverage_medium_label),
        value = favorite.cloudCoverageMedium,
        unit = favorite.cloudCoverageMediumUnit,
        status = favorite.cloudCoverageMediumStatus
    )
    DetailRow(
        label = stringResource(id = R.string.cloud_coverage_low_label),
        value = favorite.cloudCoverageLow,
        unit = favorite.cloudCoverageLowUnit,
        status = favorite.cloudCoverageLowStatus
    )
    DetailRow(
        label = stringResource(id = R.string.fog_area_fraction),
        value = favorite.fogAreaFraction,
        unit = favorite.fogAreaFractionUnit,
        status = favorite.fogAreaFractionStatus
    )
    DetailRow(
        label = stringResource(id = R.string.precipitation_amount),
        value = favorite.precipitationAmount,
        unit = favorite.precipitationAmountUnit,
        status = favorite.precipitationAmountStatus
    )
}
