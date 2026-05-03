package com.cardinal.core.data

import com.cardinal.core.domain.BoundingBox
import com.cardinal.core.domain.GeoPoint
import kotlinx.coroutines.flow.Flow

interface MapRepository {
    fun currentLocation(): Flow<GeoPoint?>
    fun heading(): Flow<Float>
    fun currentSpeedMps(): Flow<Float>
    suspend fun currentRoadName(): String?
    suspend fun currentWayId(): Long?
    suspend fun lastKnownLocation(): GeoPoint?
    suspend fun requestSingleLocation(): GeoPoint?
}
