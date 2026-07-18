package com.bakudapa.adventure.feature.summit.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SummitLogDao {
    @Query("SELECT * FROM pending_summit_logs ORDER BY createdAt ASC")
    fun getPendingLogs(): Flow<List<PendingSummitLog>>

    @Query("SELECT * FROM pending_summit_logs ORDER BY createdAt ASC")
    suspend fun getPendingLogsOnce(): List<PendingSummitLog>

    @Insert
    suspend fun insert(log: PendingSummitLog)

    @Query("DELETE FROM pending_summit_logs WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT COUNT(*) FROM pending_summit_logs")
    suspend fun count(): Int
}
