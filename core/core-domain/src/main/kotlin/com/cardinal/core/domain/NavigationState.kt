package com.cardinal.core.domain

data class NavigationState(
    val phase: Phase = Phase.IDLE,
    val currentLocation: GeoPoint? = null,
    val currentSpeedMps: Float = 0f,
    val currentSpeedLimitMps: Float? = null,
    val currentRoadName: String? = null,
    val currentGradePercent: Float = 0f,
    val heading: Float = 0f,
    val activeRoute: Route? = null,
    val nextStep: RouteStep? = null,
    val distanceToNextStepMeters: Int? = null,
    val etaEpochMs: Long? = null,
    val trafficSegments: List<TrafficSegment> = emptyList(),
    val nearbyPois: List<PoiItem> = emptyList()
) {
    enum class Phase {
        IDLE,
        ROUTING,
        ACTIVE,
        ARRIVED
    }
}
