package no.uio.ifi.in2000.team39.in2000_project.model.grib

import kotlinx.serialization.Serializable

/**
 * Represents profile information retrieved from a GRIB2 endpoint.
 */
@Serializable
data class Profiles(
    val endpoint: String,
    val params: Params,
    val updated: String,
    val uri: String,
)

/**
 * Contains parameters associated with a GRIB2 profile.
 */
@Serializable
data class Params(
    val area: String,
    val time: String,
)

/**
 * Holds information about the grid, including maximum wind speed and wind shear.
 */
data class GridInfo(
    val maxWind: Double?,
    val maxWindShear: Double?,
)
