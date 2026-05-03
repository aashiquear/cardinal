package com.cardinal.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "driven_segments")
data class DrivenSegmentEntity(
    @PrimaryKey
    val wayId: Long,
    val lastDrivenEpochDay: Int
)
