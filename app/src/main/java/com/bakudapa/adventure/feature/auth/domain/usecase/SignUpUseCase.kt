package com.bakudapa.adventure.feature.auth.domain.usecase

import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.auth.domain.model.AuthUser
import com.bakudapa.adventure.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): DataResult<AuthUser> {
        if (email.isBlank() || password.length < 6) {
            return DataResult.Error(Exception("Invalid email or password (min 6 chars)"))
        }
        return repository.signUp(email, password)
    }
}
