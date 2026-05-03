package com.cardinal.core.data

import com.cardinal.core.domain.DrivenSegment
import kotlinx.coroutines.flow.Flow

interface DrivenRoadRepository {
    fun drivenSegments(): Flow<List<DrivenSegment>>
    suspend fun recordWay(wayId: Long)
    suspend fun clearHistory()
}
