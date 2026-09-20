package no.uio.ifi.in2000.team39.in2000_project.ui.map

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

/**
 * ViewModel for managing map-related data, specifically marker positions and input coordinates.
 */
class MapViewModel : ViewModel() {
    private val _markerPosition = mutableStateOf(LatLng(59.9000, 10.7500))
    val markerPosition: State<LatLng?> = _markerPosition

    private val _latitudeInput = mutableStateOf("")
    private val _longitudeInput = mutableStateOf("")
    private val _altitudeInput = mutableStateOf("")

    val latitude: State<String> = _latitudeInput
    val longitude: State<String> = _longitudeInput
    val altitude: State<String> = _altitudeInput

    init {
        updateMarkerPositionWithInputs()
    }

    /**
     * Recalculates and updates the marker's position to reflect the latest valid latitude and longitude provided by the user.
     */
    private fun updateMarkerPositionWithInputs() {
        _latitudeInput.value.toDoubleOrNull()?.let { lat ->
            _longitudeInput.value.toDoubleOrNull()?.let { lon ->
                _markerPosition.value = LatLng(lat, lon)
            }
        }
    }

    /**
     * Directly sets the map marker to a new position based on a [LatLng] object.
     */
    fun updateMarkerPosition(position: LatLng) {
        _markerPosition.value = position
    }

    /**
     * Updates the [altitude] input, ensuring it consists of numeric characters and a decimal point.
     * Shows an error message via [snackbarHostState] if the input is invalid.
     */
    fun updateAltitudeInput(altitude: String, snackbarHostState: SnackbarHostState) {
        _altitudeInput.value = altitude

        updateInput(
            input = altitude,
            state = _altitudeInput,
            snackbarHostState = snackbarHostState,
            errorMessage = "Invalid altitude input. Please enter a valid number."
        )
    }

    /**
     * Updates the [latitude] input, ensuring it consists of numeric characters and a decimal point.
     * Recalculates the marker position upon valid input. Shows an error message via [snackbarHostState] if the input is invalid.
     */
    fun updateLatitudeInput(latitude: String, snackbarHostState: SnackbarHostState) {
        _latitudeInput.value = latitude

        updateInput(
            input = latitude,
            state = _latitudeInput,
            snackbarHostState = snackbarHostState,
            errorMessage = "Invalid latitude input. Please enter a valid number."
        )
        updateMarkerPositionWithInputs()
    }

    /**
     * Updates the [longitude] input, ensuring it consists of numeric characters and a decimal point.
     * Recalculates the marker position upon valid input. Shows an error message via [snackbarHostState] if the input is invalid.
     */
    fun updateLongitudeInput(longitude: String, snackbarHostState: SnackbarHostState) {
        _longitudeInput.value = longitude

        updateInput(
            input = longitude,
            state = _longitudeInput,
            snackbarHostState = snackbarHostState,
            errorMessage = "Invalid longitude input. Please enter a valid number."
        )
        updateMarkerPositionWithInputs()
    }

    /**
     * Validates and updates the [input] and updates the [state], showing an [errorMessage] if invalid
     * via the [snackbarHostState].
     */
    private fun updateInput(
        input: String,
        state: MutableState<String>,
        snackbarHostState: SnackbarHostState,
        errorMessage: String
    ) {
        if (validateInput(input)) {
            state.value = input
        } else {
            showErrorSnackbar(snackbarHostState, errorMessage)
        }
    }

    /**
     * Shows a Snackbar with an error [message] via the [snackbarHostState].
     */
    private fun showErrorSnackbar(snackbarHostState: SnackbarHostState, message: String) {
        viewModelScope.launch {
            snackbarHostState.showSnackbar(message, duration = SnackbarDuration.Short)
        }
    }

    /**
     * Validates the [input], ensuring it contains only numeric characters and a decimal point
     * returning true or false.
     */
    private fun validateInput(input: String): Boolean {
        return input.all { it.isDigit() || it == '.' }
    }

    /**
     * Navigates with the [navController] to a specified [screenName] if the coordinates are valid,
     * showing error messages otherwise via the [snackbarHostState].
     */
    fun navigateToScreen(
        navController: NavController,
        screenName: String,
        snackbarHostState: SnackbarHostState
    ) {
        viewModelScope.launch {
            if (validateCoordinates(snackbarHostState)) {
                navController.navigate("$screenName/${_latitudeInput.value}/${_longitudeInput.value}/${_altitudeInput.value}")
            }
        }
    }

    /**
     * Validates the latitude, longitude, and altitude, showing error messages via [snackbarHostState]
     * if any values are invalid returning true or false.
     */
    private fun validateCoordinates(snackbarHostState: SnackbarHostState): Boolean {
        val validations = listOf(
            ::isValidLatitude to "Invalid latitude. Please enter a value between -90 and 90.",
            ::isValidLongitude to "Invalid longitude. Please enter a value between -180 and 180.",
            ::isValidAltitude to "Invalid altitude. Please enter a number above 0."
        )

        for ((validate, message) in validations) {
            if (!validate()) {
                showErrorSnackbar(snackbarHostState, message)
                return false
            }
        }
        return true
    }

    /**
     * Checks if the current latitude value is valid returning true or false.
     */
    private fun isValidLatitude(): Boolean {
        return _latitudeInput.value.toDoubleOrNull()?.let { it in -90.0..90.0 } ?: false
    }

    /**
     * Checks if the current longitude value is valid returning true or false.
     */
    private fun isValidLongitude(): Boolean {
        return _longitudeInput.value.toDoubleOrNull()?.let { it in -180.0..180.0 } ?: false
    }

    /**
     * Checks if the current altitude value is valid returning true or false.
     */
    private fun isValidAltitude(): Boolean {
        return _altitudeInput.value.toDoubleOrNull()?.let { it > 0 } ?: false
    }
}
