package no.uio.ifi.in2000.team39.in2000_project.data.grib

import android.util.Log
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import no.uio.ifi.in2000.team39.in2000_project.model.grib.Profiles
import no.uio.ifi.in2000.team39.in2000_project.ui.shared.network.NetworkClient
import ucar.nc2.dt.grid.GridDataset
import java.io.File
import java.io.InputStream

/**
 * Manages retrieval and processing of GRIB2 weather data files.
 * This class fetches weather data from network services and processes it into usable formats.
 */
class GRIB2DataSource {
    private val client = NetworkClient.client

    /**
     * Fetches GRIB2 profiles from the specified [url]. Profiles contain parameters like time,
     * essential for associating fetched profiles for processing.
     *
     * Logs network exceptions and rethrows them as runtime exceptions.
     */
    private suspend fun fetchGRIB2Profiles(url: String): List<Profiles> = try {
        client.get(url).body()
    } catch (e: Exception) {
        Log.e("GRIB2DataSource", "Failed to fetch GRIB2 profiles: ${e.message}", e)
        throw RuntimeException("Error fetching GRIB2 profiles", e)
    }

    /**
     * Saves the [inputStream] to a temporary file. Used for handling large data files
     * fetched over the network. Marks files for deletion on exit to minimize disk usage.
     */
    private suspend fun saveStreamToFile(inputStream: InputStream): File =
        withContext(Dispatchers.IO) {
            val tempFile = File.createTempFile("grib2", ".bin")
            tempFile.outputStream().use { outputStream -> inputStream.copyTo(outputStream) }
            tempFile.apply { deleteOnExit() }
        }

    /**
     * Fetches and processes GRIB2 profiles, mapping them by their time for easy access.
     * This method utilizes [fetchGRIB2Profiles] to obtain the data and then maps it appropriately.
     * Returns a map where each key is a time identifier and each value is a corresponding Profiles object.
     */
    private suspend fun fetchGRIB2(): Map<String, Profiles> {
        val url = "weatherapi/isobaricgrib/1.0/available.json?type=grib2"
        val profiles: List<Profiles> = fetchGRIB2Profiles(url)
        return profiles.associateBy { it.params.time }
    }

    /**
     * Fetches and processes GRIB2 profiles, mapping them by time.
     * Uses [fetchGRIB2Profiles] to obtain data and maps it appropriately. Returns a map with time
     * as key and corresponding Profiles object as value.
     */
    suspend fun fetchGRIB2Files(): Map<String, GridDataset> = coroutineScope {
        val profilesMap = fetchGRIB2()

        profilesMap.map { (time, profile) ->
            async(Dispatchers.IO) {
                val tempFile = saveStreamToFile(client.get(profile.uri).body())
                time to GridDataset.open(tempFile.path)
            }
        }.awaitAll().toMap()
    }
}
