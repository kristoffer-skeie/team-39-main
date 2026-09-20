package no.uio.ifi.in2000.team39.in2000_project.ui.map

import android.content.res.Resources
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import kotlinx.coroutines.flow.map
import no.uio.ifi.in2000.team39.in2000_project.R
import no.uio.ifi.in2000.team39.in2000_project.ui.shared.theme.IS_DARK_THEME_ENABLED_KEY
import no.uio.ifi.in2000.team39.in2000_project.ui.shared.theme.dataStore

/**
 * Displays the main content of the map, including map interactions and markers. This composable
 * fills the available size specified by the [modifier] and uses the [cameraPositionState] to manage
 * the camera's position on the map.
 *
 * The map will display a marker at the [markerPosition], if provided. Clicking on the map will
 * trigger the [onMapClick] callback, which updates the map's state through the [mapViewModel].
 *
 * The appearance of the map adapts to the user's theme preference, using a dark style if the
 * dark theme is enabled. This preference is stored in a data store accessed via the context.
 *
 * A [snackbarHostState] is provided to display messages, such as updates to latitude and
 * longitude values when the map is clicked.
 */
@Composable
fun MapMainContent(
    modifier: Modifier,
    cameraPositionState: CameraPositionState,
    markerPosition: LatLng?,
    onMapClick: (LatLng) -> Unit,
    mapViewModel: MapViewModel,
    snackbarHostState: SnackbarHostState,
) {
    val context = LocalContext.current

    val isDarkThemeEnabled by context.dataStore.data
        .map { preferences -> preferences[IS_DARK_THEME_ENABLED_KEY] ?: false }
        .collectAsState(initial = false)

    val darkModeStyle = if (isDarkThemeEnabled) {
        try {
            MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_dark)
        } catch (e: Resources.NotFoundException) {
            null
        }
    } else {
        null
    }

    fun Double.format(digits: Int) = "%.${digits}f".format(this)

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            mapStyleOptions = darkModeStyle
        ),
        onMapClick = { latLng ->
            onMapClick(latLng)
            mapViewModel.updateLongitudeInput(latLng.longitude.format(4), snackbarHostState)
            mapViewModel.updateLatitudeInput(latLng.latitude.format(4), snackbarHostState)
        }
    ) {
        markerPosition?.let { latLng ->
            Marker(
                state = MarkerState(position = latLng),
                title = stringResource(R.string.marker_title),
                snippet = stringResource(
                    R.string.lat_lng_description,
                    latLng.latitude,
                    latLng.longitude
                )
            )
        }
    }
}
