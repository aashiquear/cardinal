package com.cardinal.core.data

import com.cardinal.core.domain.BoundingBox
import com.cardinal.core.domain.TrafficSegment
import kotlinx.coroutines.flow.Flow

interface TrafficRepository {
    fun trafficFlow(bbox: BoundingBox): Flow<List<TrafficSegment>>
}
