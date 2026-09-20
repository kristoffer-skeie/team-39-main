package no.uio.ifi.in2000.team39.in2000_project.model.favorite

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity class representing a favorite forecast item in the database.
 * Each instance corresponds to a row in the "favorites_forecast" table.
 */
@Entity(tableName = "favorites_forecast")
data class FavoriteForecastItemEntity(
    @PrimaryKey val compositeKey: String,
    val time: String,
    val lat: Double,
    val lon: Double,
    val altitude: Double,
    val groundWindSpeed: Double?,
    val groundWindSpeedUnit: String?,
    val groundWindSpeedStatus: String?,
    val gustWindSpeed: Double?,
    val gustWindSpeedUnit: String?,
    val gustWindSpeedStatus: String?,
    val maxWindShear: Double?,
    val maxWindShearUnit: String?,
    val maxWindShearStatus: String?,
    val maxAirWindSpeed: Double?,
    val maxAirWindSpeedUnit: String?,
    val maxAirWindSpeedStatus: String?,
    val cloudCoverageHigh: Double?,
    val cloudCoverageHighUnit: String?,
    val cloudCoverageHighStatus: String?,
    val cloudCoverageMedium: Double?,
    val cloudCoverageMediumUnit: String?,
    val cloudCoverageMediumStatus: String?,
    val cloudCoverageLow: Double?,
    val cloudCoverageLowUnit: String?,
    val cloudCoverageLowStatus: String?,
    val fogAreaFraction: Double?,
    val fogAreaFractionUnit: String?,
    val fogAreaFractionStatus: String?,
    val precipitationAmount: Double?,
    val precipitationAmountUnit: String?,
    val precipitationAmountStatus: String?,
    val relativeHumidity: Double?,
    val relativeHumidityUnit: String?,
    val relativeHumidityStatus: String?,
    val summary: String?,
) {
    companion object {
        /**
         * Creates and returns a composite key for the forecast item.
         * The key is a string formed by concatenating [time], [lon], [lat], and [altitude] with underscores.
         */
        fun createCompositeKey(
            time: String, lon: Double, lat: Double, altitude: Double
        ): String = listOf(time, lon, lat, altitude).joinToString(separator = "_")
    }
}
