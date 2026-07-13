package com.bakudapa.adventure.feature.auth.domain.usecase

import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.auth.domain.model.AuthUser
import com.bakudapa.adventure.feature.auth.domain.repository.AuthRepository
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): DataResult<AuthUser> {
        if (email.isBlank() || password.isBlank()) {
            return DataResult.Error(Exception("Email and password cannot be empty"))
        }
        return repository.signIn(email, password)
    }
}
