package no.uio.ifi.in2000.team39.in2000_project.ui.map

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState
import no.uio.ifi.in2000.team39.in2000_project.R
import no.uio.ifi.in2000.team39.in2000_project.ui.shared.components.BottomBar
import no.uio.ifi.in2000.team39.in2000_project.ui.shared.components.TopBar

/**
 * Displays a map screen with interactive features such as marker placement,
 * navigation to settings, and data entry for latitude, longitude, and altitude.
 * The screen also includes a floating action button that activates a modal bottom sheet for detailed input.
 * Uses [navController] for navigation and [mapViewModel] to manage map-related state and data.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    navController: NavController,
    mapViewModel: MapViewModel = viewModel(),
) {
    val cameraPositionState = rememberCameraPositionState {
        position = mapViewModel.markerPosition.value?.let {
            CameraPosition.fromLatLngZoom(it, 10f)
        } ?: CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 10f)
    }

    val markerPosition = mapViewModel.markerPosition.value

    remember {
        if (markerPosition != null) {
            mapViewModel.updateMarkerPosition(markerPosition)
        }
        null
    }

    LaunchedEffect(markerPosition) {
        markerPosition?.let {
            cameraPositionState.position =
                CameraPosition.fromLatLngZoom(it, cameraPositionState.position.zoom)
        }
    }

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val snackbarHostState = remember { SnackbarHostState() }
    val settingsRoute = stringResource(id = R.string.setting_screen_route)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        topBar = {
            TopBar(
                title = stringResource(id = R.string.map_screen_title),
                onSettingsClick = { navController.navigate(settingsRoute) },
                onBackClick = { navController.popBackStack() }
            )
        },
        bottomBar = { BottomBar(navController) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                text = { Text(stringResource(id = R.string.map_screen_floating_button_text)) },
                icon = {
                    Icon(
                        Icons.Filled.ExpandMore,
                        contentDescription = stringResource(id = R.string.map_screen_floating_button_description)
                    )
                },
                onClick = { showBottomSheet = true },
                modifier = Modifier.defaultMinSize(48.dp)
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) { paddingValues ->
        MapMainContent(
            modifier = Modifier.padding(paddingValues),
            cameraPositionState = cameraPositionState,
            markerPosition = markerPosition,
            onMapClick = mapViewModel::updateMarkerPosition,
            mapViewModel = mapViewModel,
            snackbarHostState = snackbarHostState
        )

        if (showBottomSheet) {
            InputBottomSheet(
                sheetState = sheetState,
                mapViewModel = mapViewModel,
                navController = navController,
                snackbarHostState = snackbarHostState,
                onDismiss = { showBottomSheet = false }
            )
        }
    }
}

/**
 * Modal bottom sheet for input fields to update latitude, longitude, and altitude.
 * Takes a [sheetState] to control the modal state, the [mapViewModel] to handle updates,
 * the [navController] for navigation, the [snackbarHostState] for showing snackbars,
 * and an [onDismiss] function to close the modal.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputBottomSheet(
    sheetState: SheetState,
    mapViewModel: MapViewModel,
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 25.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            InputFields(mapViewModel = mapViewModel, snackbarHostState = snackbarHostState)
            NavigateButton(
                mapViewModel = mapViewModel,
                navController = navController,
                snackbarHostState = snackbarHostState
            )
            SnackbarHost(hostState = snackbarHostState)
        }
    }
}

/**
 * Renders input fields for latitude, longitude, and altitude using [mapViewModel] to handle updates
 * and [snackbarHostState] to show snackbars for user feedback.
 */
@Composable
fun InputFields(
    mapViewModel: MapViewModel,
    snackbarHostState: SnackbarHostState
) {
    val latitudeInput by mapViewModel.latitude
    val longitudeInput by mapViewModel.longitude
    val altitudeInput by mapViewModel.altitude

    InputTextField(
        label = stringResource(R.string.latitude_text_field_label),
        value = latitudeInput,
        onValueChange = { newValue ->
            mapViewModel.updateLatitudeInput(
                newValue,
                snackbarHostState
            )
        }
    )

    InputTextField(
        label = stringResource(R.string.longitude_text_field_label),
        value = longitudeInput,
        onValueChange = { newValue ->
            mapViewModel.updateLongitudeInput(
                newValue,
                snackbarHostState
            )
        }
    )

    InputTextField(
        label = stringResource(R.string.altitude_text_field_label),
        value = altitudeInput,
        onValueChange = { newValue ->
            mapViewModel.updateAltitudeInput(
                newValue,
                snackbarHostState
            )
        }
    )
}

/**
 * Button to navigate to the weather forecast screen.
 * Uses [mapViewModel] to handle navigation, [navController] to navigate, and [snackbarHostState] to show feedback.
 */
@Composable
fun NavigateButton(
    mapViewModel: MapViewModel,
    navController: NavController,
    snackbarHostState: SnackbarHostState
) {
    val weatherForecastScreenRoute = stringResource(id = R.string.weather_forecast_screen_route)

    ElevatedButton(
        onClick = {
            mapViewModel.navigateToScreen(
                navController = navController,
                screenName = weatherForecastScreenRoute,
                snackbarHostState = snackbarHostState,
            )
        },
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .padding(horizontal = 15.dp)
            .padding(bottom = 50.dp, top = 25.dp)
            .defaultMinSize(48.dp),
        shape = RoundedCornerShape(5.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
        elevation = ButtonDefaults.elevatedButtonElevation(5.dp),
    ) {
        Text(
            text = stringResource(id = R.string.check_forecast_button),
            color = MaterialTheme.colorScheme.onTertiary,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Renders a text field with a label for numeric input.
 * Takes a [label] for the text field, a [value] for the current text, and [onValueChange] to handle text updates.
 */
@Composable
fun InputTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
) {
    val containerColor = MaterialTheme.colorScheme.secondary
    val onContainerColor = MaterialTheme.colorScheme.onSecondary

    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = label, color = onContainerColor) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 50.dp, vertical = 0.dp)
            .padding(top = 10.dp)
            .clip(shape = RoundedCornerShape(8.dp)),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = containerColor,
            unfocusedContainerColor = containerColor,
            disabledContainerColor = containerColor,
            focusedTextColor = onContainerColor,
            unfocusedTextColor = onContainerColor,
            cursorColor = onContainerColor,
        ),
    )
}
