package com.bakudapa.adventure.feature.trail.data.repository

import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.data.remote.firebase.FirestoreManager
import com.bakudapa.adventure.feature.trail.domain.model.TrailUpload
import com.bakudapa.adventure.feature.trail.domain.repository.TrailUploadRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrailUploadRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestoreManager: FirestoreManager
) : TrailUploadRepository {

    override suspend fun uploadTrail(trail: TrailUpload): DataResult<Unit> {
        return try {
            val user = auth.currentUser ?: throw Exception("User not authenticated")
            val data = hashMapOf(
                "name" to trail.name,
                "mountainId" to trail.mountainId,
                "mountainName" to trail.mountainName,
                "difficulty" to trail.difficulty,
                "durationMinutes" to trail.durationMinutes,
                "distanceKm" to trail.distanceKm,
                "imageUrl" to trail.imageUrl,
                "description" to trail.description,
                "elevationGain" to trail.elevationGain,
                "maxElevation" to trail.maxElevation,
                "recommendedGear" to trail.recommendedGear,
                "pointsOfInterest" to trail.pointsOfInterest.map { poi ->
                    hashMapOf(
                        "name" to poi.name,
                        "type" to poi.type.name,
                        "latitude" to poi.latitude,
                        "longitude" to poi.longitude,
                        "elevation" to poi.elevation,
                        "description" to poi.description,
                    )
                },
                "status" to "pending",
                "authorId" to user.uid,
                "authorName" to (user.displayName ?: "Anonymous"),
                "popularity" to 0,
                "createdAt" to FieldValue.serverTimestamp(),
            )
            firestoreManager.getCollection("trails").add(data).await()
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }
}
