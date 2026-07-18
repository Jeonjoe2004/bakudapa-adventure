package com.bakudapa.adventure.feature.summit.domain.repository

import android.net.Uri
import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.summit.domain.model.SummitLog
import kotlinx.coroutines.flow.Flow

interface SummitRepository {
    fun getSummitLogs(mountainId: String): Flow<DataResult<List<SummitLog>>>
    suspend fun createSummitLog(mountainId: String, mountainName: String, caption: String, photoUri: Uri?): DataResult<Unit>
    suspend fun checkInRadius(mountainId: String, latitude: Double, longitude: Double): Boolean
    suspend fun getPendingCount(): Int
}
