package no.uio.ifi.in2000.team39.in2000_project

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.State
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.gms.maps.model.LatLng
import no.uio.ifi.in2000.team39.in2000_project.ui.map.MapViewModel
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Test suite for MapViewModel that ensures the ViewModel handles map-related functionalities correctly.
 * These tests verify that the ViewModel updates and maintains the state of map markers and user inputs accurately.
 */
@RunWith(AndroidJUnit4::class)
class MapViewModelTest {
    /**
     * Tests if the MapViewModel updates the marker position correctly based on user input.
     * This test ensures that latitude and longitude inputs are correctly converted and stored as a LatLng object.
     */
    @Test
    fun testMarkerPosition() {
        val mapView = MapViewModel()
        mapView.updateLongitudeInput(longitude = "5", snackbarHostState = SnackbarHostState())
        mapView.updateLatitudeInput(latitude = "3", snackbarHostState = SnackbarHostState())

        val markerPosition: State<LatLng?> = mapView.markerPosition
        val markerPositionValue: LatLng? = markerPosition.value

        // Assert that the marker position is not null and the coordinates are as expected
        Assert.assertEquals(true, markerPositionValue != null)
        markerPositionValue?.let {
            Assert.assertEquals(3.0, it.latitude, 0.0)
            Assert.assertEquals(5.0, it.longitude, 0.0)
        }
    }

    /**
     * Tests the default values of latitude, longitude, and altitude in MapViewModel.
     * This test verifies that the initial values are set to empty strings, indicating no user input or defaults set.
     */
    @Test
    fun testDefaultValues() {

        val mapViewModel = MapViewModel()

        val latitude: State<String> = mapViewModel.latitude
        val longitude: State<String> = mapViewModel.longitude
        val altitude: State<String> = mapViewModel.altitude

        // Assert that the default values for latitude, longitude, and altitude are empty
        Assert.assertEquals("", latitude.value)
        Assert.assertEquals("", longitude.value)
        Assert.assertEquals("", altitude.value)
    }
}
