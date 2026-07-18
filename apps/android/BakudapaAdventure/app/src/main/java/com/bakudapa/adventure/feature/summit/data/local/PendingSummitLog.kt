package com.bakudapa.adventure.feature.summit.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_summit_logs")
data class PendingSummitLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val mountainId: String,
    val mountainName: String,
    val caption: String,
    val photoUrl: String?,
    val createdAt: Long = System.currentTimeMillis()
)
