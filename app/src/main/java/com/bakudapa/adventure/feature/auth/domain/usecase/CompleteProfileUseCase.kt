package com.bakudapa.adventure.feature.auth.domain.usecase

import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class CompleteProfileUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(displayName: String, photoUrl: String? = null): DataResult<Unit> {
        if (displayName.length < 3) {
            return DataResult.Error(Exception("Display name must be at least 3 characters"))
        }
        return repository.completeProfile(displayName, photoUrl)
    }
}
