package com.cardinal.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cardinal.data.local.entity.DrivenSegmentEntity

@Database(
    entities = [DrivenSegmentEntity::class],
    version = 1,
    exportSchema = true
)
abstract class CardinalDatabase : RoomDatabase() {
    abstract fun drivenSegmentDao(): DrivenSegmentDao
}
