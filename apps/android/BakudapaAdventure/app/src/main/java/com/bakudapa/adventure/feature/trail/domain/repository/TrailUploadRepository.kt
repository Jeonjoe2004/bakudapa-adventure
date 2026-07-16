package com.bakudapa.adventure.feature.trail.domain.repository

import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.trail.domain.model.TrailUpload

interface TrailUploadRepository {
    suspend fun uploadTrail(trail: TrailUpload): DataResult<Unit>
}
