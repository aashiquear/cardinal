package com.cardinal.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cardinal.data.local.entity.DrivenSegmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DrivenSegmentDao {
    @Query("SELECT * FROM driven_segments WHERE lastDrivenEpochDay >= :minDay")
    fun recentSegments(minDay: Int): Flow<List<DrivenSegmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(segment: DrivenSegmentEntity)

    @Query("DELETE FROM driven_segments WHERE lastDrivenEpochDay < :minDay")
    suspend fun purgeOld(minDay: Int)
}
