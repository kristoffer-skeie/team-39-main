package no.uio.ifi.in2000.team39.in2000_project.model.weather

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a weather feature with type, geometry, and properties.
 */
@Serializable
data class WeatherFeature(
    val type: String,
    val geometry: Geometry,
    val properties: Properties,
)

/**
 * Represents the geometry of a weather feature.
 * Contains type and coordinates.
 */
@Serializable
data class Geometry(
    val type: String,
    val coordinates: List<Double>,
)

/**
 * Represents properties of a weather feature.
 * Contains metadata and timeseries data.
 */
@Serializable
data class Properties(
    val meta: Meta,
    val timeseries: List<TimeSeries>,
)

/**
 * Contains metadata for weather properties.
 */
@Serializable
data class Meta(
    @SerialName("updated_at") val updatedAt: String,
    val units: WeatherUnits,
)


/**
 * Represents units for various weather measurements.
 */
@Serializable
data class WeatherUnits(
    @SerialName("air_pressure_at_sea_level") val airPressureAtSeaLevelUnit: String? = null,
    @SerialName("air_temperature") val airTemperatureUnit: String? = null,
    @SerialName("cloud_area_fraction_high") val cloudAreaFractionHighUnit: String? = null,
    @SerialName("cloud_area_fraction_low") val cloudAreaFractionLowUnit: String? = null,
    @SerialName("cloud_area_fraction_medium") val cloudAreaFractionMediumUnit: String? = null,
    @SerialName("fog_area_fraction") val fogAreaFractionUnit: String? = null,
    @SerialName("precipitation_amount") val precipitationAmountUnit: String? = null,
    @SerialName("relative_humidity") val relativeHumidityUnit: String? = null,
    @SerialName("wind_from_direction") val windFromDirectionUnit: String? = null,
    @SerialName("wind_speed") val windSpeedUnit: String? = null,
    @SerialName("wind_speed_of_gust") val windSpeedOfGustUnit: String? = null,
)

/**
 * Represents a time series in the weather data.
 * Contains the time and data.
 */
@Serializable
data class TimeSeries(
    val time: String,
    val data: Data,
)

/**
 * Represents data at a specific time in the time series.
 * Contains instant data and forecasts for the next hours.
 */
@Serializable
data class Data(
    val instant: Instant,
    @SerialName("next_12_hours") val next12Hours: NextHoursForecast? = null,
    @SerialName("next_1_hours") val next1Hours: NextHoursForecast? = null,
    @SerialName("next_6_hours") val next6Hours: NextHoursForecast? = null,
)

/**
 * Represents instant weather details.
 */
@Serializable
data class Instant(
    val details: Details,
)

/**
 * Represents detailed weather measurements at an instant.
 */
@Serializable
data class Details(
    @SerialName("air_pressure_at_sea_level") val airPressureAtSeaLevel: Double? = null,
    @SerialName("air_temperature") val airTemperature: Double? = null,
    @SerialName("cloud_area_fraction_high") val cloudAreaFractionHigh: Double? = null,
    @SerialName("cloud_area_fraction_low") val cloudAreaFractionLow: Double? = null,
    @SerialName("cloud_area_fraction_medium") val cloudAreaFractionMedium: Double? = null,
    @SerialName("fog_area_fraction") val fogAreaFraction: Double? = null,
    @SerialName("relative_humidity") val relativeHumidity: Double? = null,
    @SerialName("wind_from_direction") val windFromDirection: Double? = null,
    @SerialName("wind_speed") val windSpeed: Double? = null,
    @SerialName("wind_speed_of_gust") val windSpeedOfGust: Double? = null,
)

/**
 * Represents a weather forecast for the next hours.
 */
@Serializable
data class NextHoursForecast(
    val summary: ForecastSummary,
    val details: NextHoursDetails? = null,
)

/**
 * Provides a summary of the weather forecast.
 */
@Serializable
data class ForecastSummary(
    @SerialName("symbol_code") val symbolCode: String,
)

/**
 * Provides detailed information for the next hours forecast.
 */
@Serializable
data class NextHoursDetails(
    @SerialName("precipitation_amount") val precipitationAmount: Double? = null,
)
