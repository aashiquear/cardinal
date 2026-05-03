package com.cardinal.core.data.repository

import com.cardinal.core.data.PoiRepository
import com.cardinal.core.domain.GeoPoint
import com.cardinal.core.domain.PoiItem
import com.cardinal.core.domain.PoiType
import com.cardinal.data.remote.api.OverpassApi
import com.cardinal.data.remote.dto.OverpassElement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

@Singleton
class PoiRepositoryImpl @Inject constructor(
    private val overpassApi: OverpassApi
) : PoiRepository {

    override fun nearbyPois(
        location: GeoPoint,
        types: List<PoiType>,
        radiusMeters: Int
    ): Flow<List<PoiItem>> = flow {
        try {
            val query = buildOverpassQuery(location, types, radiusMeters)
            val response = overpassApi.query(query)
            val pois = response.elements.mapNotNull { element ->
                elementToPoiItem(element, location)
            }.sortedBy { it.distanceMeters }.take(3)
            emit(pois)
        } catch (_: Exception) {
            emit(emptyList())
        }
    }

    private fun buildOverpassQuery(location: GeoPoint, types: List<PoiType>, radius: Int): String {
        val filters = types.flatMap { type ->
            when (type) {
                PoiType.GAS -> listOf("node[\"amenity\"=\"fuel\"](around:$radius,${location.lat},${location.lng})")
                PoiType.EV_CHARGING -> listOf("node[\"amenity\"=\"charging_station\"](around:$radius,${location.lat},${location.lng})")
                PoiType.REST_AREA -> listOf(
                    "node[\"highway\"=\"rest_area\"](around:$radius,${location.lat},${location.lng})",
                    "node[\"amenity\"=\"restarea\"](around:$radius,${location.lat},${location.lng})"
                )
                PoiType.FOOD -> listOf("node[\"amenity\"=\"restaurant\"](around:$radius,${location.lat},${location.lng})")
                PoiType.COFFEE -> listOf("node[\"amenity\"=\"cafe\"](around:$radius,${location.lat},${location.lng})")
                PoiType.SHOPPING -> listOf("node[\"shop\"](around:$radius,${location.lat},${location.lng})")
                PoiType.PHARMACY -> listOf("node[\"amenity\"=\"pharmacy\"](around:$radius,${location.lat},${location.lng})")
            }
        }
        return buildString {
            append("[out:json][timeout:10];")
            append("(")
            filters.forEach { append(it); append(";") }
            append(");")
            append("out body 10;")
        }
    }

    private fun elementToPoiItem(element: OverpassElement, userLocation: GeoPoint): PoiItem? {
        val lat = element.lat ?: return null
        val lon = element.lon ?: return null
        val tags = element.tags ?: return null

        val name = tags.name ?: tags.brand ?: tags.operator ?: return null
        val type = when {
            tags.amenity == "fuel" -> PoiType.GAS
            tags.amenity == "charging_station" -> PoiType.EV_CHARGING
            tags.highway == "rest_area" || tags.amenity == "restarea" -> PoiType.REST_AREA
            tags.amenity == "restaurant" -> PoiType.FOOD
            tags.amenity == "cafe" -> PoiType.COFFEE
            tags.shop != null -> PoiType.SHOPPING
            tags.amenity == "pharmacy" -> PoiType.PHARMACY
            else -> return null
        }
        val distance = haversineDistanceMeters(userLocation.lat, userLocation.lng, lat, lon).roundToInt()

        return PoiItem(
            id = element.id.toString(),
            type = type,
            name = name,
            location = GeoPoint(lat, lon),
            distanceMeters = distance
        )
    }

    private fun haversineDistanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = kotlin.math.sin(dLat / 2) * kotlin.math.sin(dLat / 2) +
                kotlin.math.cos(Math.toRadians(lat1)) * kotlin.math.cos(Math.toRadians(lat2)) *
                kotlin.math.sin(dLon / 2) * kotlin.math.sin(dLon / 2)
        val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))
        return r * c
    }
}
