package com.cardinal.core.domain

import kotlinx.serialization.Serializable

@Serializable
data class Route(
    val id: String,
    val polyline: List<GeoPoint>,
    val steps: List<RouteStep>,
    val distanceMeters: Int,
    val durationSeconds: Int,
    val destination: Place
)
