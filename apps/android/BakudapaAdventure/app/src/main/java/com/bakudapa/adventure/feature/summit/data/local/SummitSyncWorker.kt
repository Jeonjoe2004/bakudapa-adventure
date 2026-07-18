package com.bakudapa.adventure.feature.summit.data.local

import android.content.Context
import android.location.Location
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.gms.location.LocationServices
import com.google.firebase.functions.FirebaseFunctions
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await

@HiltWorker
class SummitSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val dao = SummitLogDatabase.getInstance(applicationContext).summitLogDao()
        val pending = dao.getPendingLogsOnce()
        if (pending.isEmpty()) return Result.success()

        val functions = FirebaseFunctions.getInstance()

        for (log in pending) {
            try {
                val data = mapOf(
                    "mountainId" to log.mountainId,
                    "mountainName" to log.mountainName,
                    "caption" to log.caption,
                    "photoUrl" to log.photoUrl
                )
                functions.getHttpsCallable("createSummitLog").call(data).await()
                dao.delete(log.id)
            } catch (e: Exception) {
                // Validasi gagal (rate limit / GPS) → hapus dari queue, user akan lihat error
                if (e.message?.contains("already-exists") == true ||
                    e.message?.contains("terlalu jauh") == true) {
                    dao.delete(log.id)
                }
                return Result.retry()
            }
        }

        return Result.success()
    }
}
