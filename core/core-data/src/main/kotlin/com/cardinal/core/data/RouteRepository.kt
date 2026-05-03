package com.cardinal.core.data

import com.cardinal.core.domain.GeoPoint
import com.cardinal.core.domain.Route

interface RouteRepository {
    suspend fun calculateRoute(origin: GeoPoint, destination: GeoPoint): Result<Route>
    suspend fun reroute(currentLocation: GeoPoint, destination: GeoPoint): Result<Route>
}
