package com.cardinal.core.domain

import kotlinx.serialization.Serializable

@Serializable
data class GeoPoint(
    val lat: Double,
    val lng: Double
)
