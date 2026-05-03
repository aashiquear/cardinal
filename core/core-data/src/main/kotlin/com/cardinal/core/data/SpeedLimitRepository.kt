package com.cardinal.core.data

import com.cardinal.core.domain.GeoPoint
import kotlinx.coroutines.flow.Flow

interface SpeedLimitRepository {
    fun speedLimitAt(location: GeoPoint): Flow<Float?>
}
