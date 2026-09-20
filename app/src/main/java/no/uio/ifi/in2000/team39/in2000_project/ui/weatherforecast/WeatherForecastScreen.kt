package no.uio.ifi.in2000.team39.in2000_project.ui.weatherforecast

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import no.uio.ifi.in2000.team39.in2000_project.R
import no.uio.ifi.in2000.team39.in2000_project.ui.shared.components.BottomBar
import no.uio.ifi.in2000.team39.in2000_project.ui.shared.components.TopBar

/**
 * Displays the main weather forecast screen. It initializes and manages the UI state based on user input coordinates.
 * This screen is structured using a Scaffold that includes a top bar for navigation and settings access,
 * and a bottom bar for additional navigation functionalities. It utilizes LaunchedEffect to initialize the weather data
 * based on provided latitude [lat], longitude [lon], and altitude [altitude].
 *
 * The [navController] allows navigating between screens. The [weatherForecastViewModel] manages the weather data and UI state.
 */
@Composable
fun WeatherForecastScreen(
    navController: NavController,
    weatherForecastViewModel: WeatherForecastViewModel = viewModel(),
    lat: String,
    lon: String,
    altitude: String,
) {
    LaunchedEffect(lat, lon, altitude) {
        weatherForecastViewModel.initialize(lat, lon, altitude)
    }

    val weatherForecastUiState by weatherForecastViewModel.weatherForecastUiState.collectAsState()
    val settingsRoute = stringResource(id = R.string.setting_screen_route)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        topBar = {
            TopBar(
                onBackClick = { navController.popBackStack() },
                onSettingsClick = { navController.navigate(settingsRoute) },
                title = stringResource(id = R.string.weather_forecast_screen_title)
            )
        },
        bottomBar = { BottomBar(navController) }
    ) { innerPadding ->
        WeatherForecastContent(
            weatherForecastUiState, innerPadding, lat, lon, altitude, weatherForecastViewModel
        )
    }
}

/**
 * Manages the content display for the weather forecast screen, including loading, success, and error UI states.
 * It displays weather data or status indicators based on the current state of the weather data fetch.
 * The layout updates dynamically to show either loading indicators, fetched weather data, or error messages accordingly.
 *
 * The content is adjusted within a padding defined by [paddingValues], and uses the coordinates [lat], [lon],
 * and [altitude] for display. Weather data handling is facilitated by [weatherForecastViewModel].
 */
@Composable
fun WeatherForecastContent(
    weatherForecastUiState: WeatherForecastUiState,
    paddingValues: PaddingValues,
    lat: String,
    lon: String,
    altitude: String,
    weatherForecastViewModel: WeatherForecastViewModel,
) {
    val formattedLatitude = String.format("%.4f", lat.toFloat())
    val formattedLongitude = String.format("%.4f", lon.toFloat())
    val locationText = stringResource(
        R.string.location_info_template, formattedLatitude, formattedLongitude, altitude
    )

    when (weatherForecastUiState.uiState) {
        UIState.LOADING -> LoadingView()
        UIState.SUCCESS -> weatherForecastUiState.weatherFeature?.let {
            WeatherForecastDisplay(
                weatherForecastUiState,
                paddingValues,
                locationText,
                weatherForecastViewModel,
                altitude
            )
        }

        UIState.ERROR -> ErrorView()
    }
}

/**
 * Displays the weather data in a structured format within the weather forecast screen.
 * This component allows users to interact with the data, including filtering out specific conditions via a toggle switch.
 *
 * The content is displayed in a column layout, starting with the location information [locationText], which is shown at the top.
 * Below that, there is a row containing a switch to toggle the filtering of "red times", based on the current filter state
 * managed by [weatherForecastViewModel]. The main weather data is displayed through `WeatherForecastMainContent` composable,
 * which includes detailed weather forecasts and additional data such as time series and grid information.
 *
 * The [weatherForecastUiState] holds the UI state and weather data to be displayed. The [paddingValues] apply padding
 * around the column layout. The [altitude] is used in the `WeatherForecastMainContent` to provide elevation specific data.
 */
@Composable
fun WeatherForecastDisplay(
    weatherForecastUiState: WeatherForecastUiState,
    paddingValues: PaddingValues,
    locationText: String,
    weatherForecastViewModel: WeatherForecastViewModel,
    altitude: String,
) {
    Column(modifier = Modifier.padding(paddingValues)) {
        Text(text = locationText, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(id = R.string.filter_switch_text))
            Switch(
                checked = weatherForecastUiState.isFiltered,
                onCheckedChange = { weatherForecastViewModel.filterNonRed() }
            )
        }

        WeatherForecastMainContent(
            timeSeries = weatherForecastViewModel.getTimeSeries(),
            evaluationResults = weatherForecastUiState.forecastResultsMap,
            gridInfoMap = weatherForecastUiState.gridInfoMap,
            weatherForecastViewModel = weatherForecastViewModel,
            altitude = altitude.toDouble(),
        )
    }
}

/**
 * Represents the view displayed while waiting for data loading, showing a circular progress indicator.
 */
@Composable
fun LoadingView() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimaryContainer)
    }
}

/**
 * Represents the error view when data loading fails, displaying an error message.
 */
@Composable
fun ErrorView() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Text(text = stringResource(id = R.string.error_view_text), color = Color.Red)
    }
}