package com.cardinal.core.domain

import kotlinx.serialization.Serializable

@Serializable
data class RouteStep(
    val maneuver: Maneuver,
    val streetName: String,
    val distanceMeters: Int,
    val laneGuidance: List<Lane>? = null
)
