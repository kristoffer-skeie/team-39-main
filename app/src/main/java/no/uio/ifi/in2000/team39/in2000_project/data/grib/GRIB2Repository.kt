package no.uio.ifi.in2000.team39.in2000_project.data.grib

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import no.uio.ifi.in2000.team39.in2000_project.model.grib.GridInfo
import ucar.nc2.dt.grid.GridDataset
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.pow
import kotlin.math.sqrt

interface GRIB2Repository {
    suspend fun getGrib2Info(
        latitude: Double, longitude: Double, altitude: Double
    ): MutableMap<String, GridInfo>
}

/**
 * Implementation of GRIB2Repository responsible for fetching and processing GRIB2 data files.
 */
class GRIB2RepositoryImplementation(
    private val grib2DataSource: GRIB2DataSource = GRIB2DataSource()
) : GRIB2Repository {
    private val _cachedGridInfoMap = MutableStateFlow<MutableMap<String, GridInfo>?>(null)
    private val cachedWeatherDetails = _cachedGridInfoMap.asStateFlow()

    /**
     * Retrieves weather information for a specific location and caches the result.
     * Processes GRIB2 files to compute maximum wind speeds and wind shears at the given [latitude], [longitude], and [altitude].
     * If cached data exists, it returns this data to avoid reprocessing.
     */
    override suspend fun getGrib2Info(
        latitude: Double, longitude: Double, altitude: Double
    ): MutableMap<String, GridInfo> {
        cachedWeatherDetails.value?.let { return it }

        val gridDatasetMap: Map<String, GridDataset> = grib2DataSource.fetchGRIB2Files()
        val outputMap = ConcurrentHashMap<String, GridInfo>()

        coroutineScope {
            gridDatasetMap.map { (time, gridDataset) ->
                async(Dispatchers.IO) {
                    val gridInfo = processGrib2File(gridDataset, latitude, longitude, altitude)
                    outputMap[time] = gridInfo
                }
            }.awaitAll()
        }

        _cachedGridInfoMap.value = outputMap
        return outputMap
    }

    /**
     * Processes a GRIB2 file to determine maximum wind speed and wind shear at specified geographic coordinates and altitude.
     * Calculates the maximum wind speed and wind shear from wind vectors extracted from the dataset at the given [latitude], [longitude], and [altitude].
     * This method uses asynchronous tasks to efficiently handle the data extraction and calculation processes.
     *
     * Returns a [GridInfo] object containing the computed maximum wind speed and maximum wind shear.
     */
    private suspend fun processGrib2File(
        gds: GridDataset, latitude: Double, longitude: Double, altitude: Double
    ): GridInfo = coroutineScope {
        val windVectors = getWindVectorsAtAltitude(gds, latitude, longitude, altitude)

        val maxWind = async(Dispatchers.IO) {
            windVectors.maxOfOrNull { (u, v) -> sqrt(u.pow(2) + v.pow(2)) }
        }

        val maxWindShear = async(Dispatchers.IO) { calculateMaxWindShear(windVectors) }

        GridInfo(maxWind.await(), maxWindShear.await())
    }

    /**
     * Computes the maximum wind shear by evaluating the rate of change in wind speed and direction between consecutive measurements.
     */
    private fun calculateMaxWindShear(windVectors: List<Pair<Double, Double>>): Double? {
        if (windVectors.size < 2) return null
        return windVectors.windowed(2, 1).maxOfOrNull { (first, second) ->
            val (uDiff, vDiff) = Pair(second.first - first.first, second.second - first.second)
            sqrt(uDiff.pow(2) + vDiff.pow(2))
        }
    }

    /**
     * Computes the altitude from atmospheric pressure and ambient temperature using the hypsometric formula.
     */
    private fun calculateAltitude(
        atmosphericPressure: Double, ambientTemperature: Double
    ): Double {
        val gasConstantForDryAir = 287.05
        val gravitationalAcceleration = 9.80665
        val seaLevelPressure = 101325.0

        return (gasConstantForDryAir * ambientTemperature / gravitationalAcceleration) * kotlin.math.ln(
            seaLevelPressure / atmosphericPressure
        )
    }

    /**
     * Retrieves the wind vector components (u, v) at a specified [altitude] for the given geographic coordinates ([latitude], [longitude]).
     * Each vector component represents the wind's speed in the respective horizontal directions.
     */
    private fun getWindVectorsAtAltitude(
        gds: GridDataset, latitude: Double, longitude: Double, altitude: Double
    ): List<Pair<Double, Double>> {
        val uGrid = gds.findGridByName("u-component_of_wind_isobaric") ?: return emptyList()
        val vGrid = gds.findGridByName("v-component_of_wind_isobaric") ?: return emptyList()
        val tempGrid = gds.findGridByName("Temperature_isobaric") ?: return emptyList()
        val coordSys = uGrid.coordinateSystem
        val xyIndex = coordSys.findXYindexFromLatLon(latitude, longitude, null)

        val verticalSize = coordSys.verticalAxis.size
        val windVectors = mutableListOf<Pair<Double, Double>>()

        for (levelIndexLong in 0 until verticalSize) {
            val levelIndex = levelIndexLong.toInt()
            val levelPressure =
                coordSys.verticalAxis.getCoordValue(levelIndex) * 100 // Convert hPa to Pa
            val tempValue =
                tempGrid.readDataSlice(0, levelIndex, xyIndex[1], xyIndex[0]).reduce().getDouble(0)
            val levelAltitude = calculateAltitude(levelPressure, tempValue)

            if (levelAltitude > altitude) {
                break
            }

            val uValue =
                uGrid.readDataSlice(0, levelIndex, xyIndex[1], xyIndex[0]).reduce().getDouble(0)
            val vValue =
                vGrid.readDataSlice(0, levelIndex, xyIndex[1], xyIndex[0]).reduce().getDouble(0)

            windVectors.add(Pair(uValue, vValue))
        }

        return windVectors
    }
}
