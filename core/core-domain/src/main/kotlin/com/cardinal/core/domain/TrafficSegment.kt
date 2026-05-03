package com.cardinal.core.domain

data class TrafficSegment(
    val polyline: List<GeoPoint>,
    val congestion: CongestionLevel,
    val speedKmh: Double? = null
)

enum class CongestionLevel {
    FREE,
    SLOW,
    HEAVY
}
