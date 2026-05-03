package com.cardinal.core.data.repository

import com.cardinal.core.data.DrivenRoadRepository
import com.cardinal.core.domain.DrivenSegment
import com.cardinal.data.local.db.DrivenSegmentDao
import com.cardinal.data.local.entity.DrivenSegmentEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DrivenRoadRepositoryImpl @Inject constructor(
    private val drivenSegmentDao: DrivenSegmentDao
) : DrivenRoadRepository {

    override fun drivenSegments(): Flow<List<DrivenSegment>> {
        val minDay = LocalDate.now().minusDays(RETENTION_DAYS).toEpochDay().toInt()
        return drivenSegmentDao.recentSegments(minDay).map { entities ->
            entities.map { DrivenSegment(it.wayId, it.lastDrivenEpochDay) }
        }
    }

    override suspend fun recordWay(wayId: Long) {
        val today = LocalDate.now().toEpochDay().toInt()
        drivenSegmentDao.insert(DrivenSegmentEntity(wayId = wayId, lastDrivenEpochDay = today))
    }

    override suspend fun clearHistory() {
        val minDay = LocalDate.now().minusDays(RETENTION_DAYS).toEpochDay().toInt()
        drivenSegmentDao.purgeOld(minDay)
    }

    companion object {
        private const val RETENTION_DAYS = 365L
    }
}
