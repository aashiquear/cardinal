package com.cardinal.core.data

import com.cardinal.core.domain.GeoPoint
import com.cardinal.core.domain.PoiItem
import com.cardinal.core.domain.PoiType
import kotlinx.coroutines.flow.Flow

interface PoiRepository {
    fun nearbyPois(location: GeoPoint, types: List<PoiType>, radiusMeters: Int = 5000): Flow<List<PoiItem>>
}
