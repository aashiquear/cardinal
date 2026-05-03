package com.cardinal.core.data.repository

import com.cardinal.core.data.GradeRepository
import com.cardinal.core.domain.GeoPoint
import com.cardinal.data.remote.api.OpenTopoDataApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GradeRepositoryImpl @Inject constructor(
    private val openTopoDataApi: OpenTopoDataApi
) : GradeRepository {

    override fun currentGrade(location: GeoPoint, heading: Float): Flow<Float> = flow {
        try {
            val ahead = pointAhead(location, heading.toDouble(), 50.0)
            val locations = "${location.lat},${location.lng}|${ahead.lat},${ahead.lng}"
            val response = openTopoDataApi.getElevation(locations)
            val results = response.results
            if (results.size >= 2) {
                val elev1 = results[0].elevation ?: 0.0
                val elev2 = results[1].elevation ?: 0.0
                val grade = ((elev2 - elev1) / 50.0 * 100.0).toFloat()
                emit(grade.coerceIn(-30f, 30f))
            } else {
                emit(0f)
            }
        } catch (_: Exception) {
            emit(0f)
        }
    }

    private fun pointAhead(location: GeoPoint, headingDegrees: Double, distanceMeters: Double): GeoPoint {
        val r = 6371000.0
        val lat1 = Math.toRadians(location.lat)
        val lon1 = Math.toRadians(location.lng)
        val bearing = Math.toRadians(headingDegrees)
        val d = distanceMeters / r

        val lat2 = Math.toDegrees(
            kotlin.math.asin(
                kotlin.math.sin(lat1) * kotlin.math.cos(d) +
                        kotlin.math.cos(lat1) * kotlin.math.sin(d) * kotlin.math.cos(bearing)
            )
        )
        val lon2 = Math.toDegrees(
            lon1 + kotlin.math.atan2(
                kotlin.math.sin(bearing) * kotlin.math.sin(d) * kotlin.math.cos(lat1),
                kotlin.math.cos(d) - kotlin.math.sin(lat1) * kotlin.math.sin(Math.toRadians(lat2))
            )
        )
        return GeoPoint(lat2, lon2)
    }
}
