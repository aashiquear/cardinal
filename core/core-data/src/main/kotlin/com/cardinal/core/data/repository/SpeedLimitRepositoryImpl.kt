package com.cardinal.core.data.repository

import com.cardinal.core.data.SpeedLimitRepository
import com.cardinal.core.domain.GeoPoint
import com.cardinal.data.remote.api.OverpassApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpeedLimitRepositoryImpl @Inject constructor(
    private val overpassApi: OverpassApi
) : SpeedLimitRepository {

    override fun speedLimitAt(location: GeoPoint): Flow<Float?> = flow {
        try {
            val query = buildString {
                append("[out:json][timeout:10];")
                append("way(around:50,${location.lat},${location.lng})[\"maxspeed\"];")
                append("out tags;")
            }
            val response = overpassApi.query(query)
            val maxSpeedTag = response.elements.firstOrNull()?.tags?.maxSpeed
            val limitMps = maxSpeedTag?.let { parseMaxSpeed(it) }
            emit(limitMps)
        } catch (_: Exception) {
            emit(null)
        }
    }

    private fun parseMaxSpeed(value: String): Float? {
        val trimmed = value.trim()
        return when {
            trimmed.endsWith("mph", ignoreCase = true) -> {
                trimmed.removeSuffix("mph").trim().toFloatOrNull()?.times(0.44704f)
            }
            trimmed.endsWith("km/h", ignoreCase = true) || trimmed.endsWith("kmh", ignoreCase = true) -> {
                trimmed.removeSuffix("km/h").removeSuffix("kmh").trim().toFloatOrNull()?.times(0.277778f)
            }
            else -> trimmed.toFloatOrNull()?.times(0.277778f)
        }
    }
}
