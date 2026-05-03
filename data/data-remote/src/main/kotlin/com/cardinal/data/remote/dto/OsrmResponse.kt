package com.cardinal.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OsrmRouteResponse(
    val routes: List<OsrmRoute> = emptyList(),
    val waypoints: List<OsrmWaypoint> = emptyList()
)

@Serializable
data class OsrmRoute(
    val geometry: String? = null,
    val legs: List<OsrmLeg> = emptyList(),
    val distance: Double = 0.0,
    val duration: Double = 0.0
)

@Serializable
data class OsrmLeg(
    val steps: List<OsrmStep> = emptyList(),
    val distance: Double = 0.0,
    val duration: Double = 0.0
)

@Serializable
data class OsrmStep(
    val maneuver: OsrmManeuver = OsrmManeuver(),
    val name: String = "",
    val distance: Double = 0.0,
    val duration: Double = 0.0,
    val geometry: String? = null
)

@Serializable
data class OsrmManeuver(
    val type: String = "",
    val modifier: String? = null,
    @SerialName("exit_number") val exitNumber: Int? = null
)

@Serializable
data class OsrmWaypoint(
    val name: String = "",
    @SerialName("location") val coordinates: List<Double> = emptyList()
)
