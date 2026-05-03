package com.cardinal.app.repository

import com.cardinal.app.location.FusedLocationDataSource
import com.cardinal.core.data.MapRepository
import com.cardinal.core.domain.GeoPoint
import com.cardinal.data.remote.api.NominatimApi
import com.cardinal.data.remote.api.OverpassApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MapRepositoryImpl @Inject constructor(
    private val locationDataSource: FusedLocationDataSource,
    private val nominatimApi: NominatimApi,
    private val overpassApi: OverpassApi
) : MapRepository {

    override fun currentLocation(): Flow<GeoPoint?> = locationDataSource.locationFlow()

    override fun heading(): Flow<Float> = locationDataSource.headingFlow()

    override fun currentSpeedMps(): Flow<Float> = locationDataSource.speedFlow()

    override suspend fun currentRoadName(): String? {
        return try {
            val location = locationDataSource.lastKnownLocation() ?: return null
            val response = nominatimApi.reverse(location.lat, location.lng)
            response.address?.road
                ?: response.address?.highway
                ?: response.address?.street
                ?: response.address?.residential
        } catch (_: Exception) {
            null
        }
    }

    override suspend fun currentWayId(): Long? {
        return try {
            val location = locationDataSource.lastKnownLocation() ?: return null
            val query = buildString {
                append("[out:json][timeout:10];")
                append("way(around:30,${location.lat},${location.lng})[\"highway\"];")
                append("out ids;")
            }
            val response = overpassApi.query(query)
            response.elements.firstOrNull()?.id
        } catch (_: Exception) {
            null
        }
    }

    override suspend fun lastKnownLocation(): GeoPoint? = locationDataSource.lastKnownLocation()

    override suspend fun requestSingleLocation(): GeoPoint? = locationDataSource.requestSingleLocation()
}
