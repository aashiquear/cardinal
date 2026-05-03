package com.cardinal.core.data

import com.cardinal.core.domain.GeoPoint
import kotlinx.coroutines.flow.Flow

interface GradeRepository {
    fun currentGrade(location: GeoPoint, heading: Float = 0f): Flow<Float>
}
