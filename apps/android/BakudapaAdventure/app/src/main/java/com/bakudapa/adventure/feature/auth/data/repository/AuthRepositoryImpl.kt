package com.bakudapa.adventure.feature.auth.data.repository

import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.data.remote.firebase.FirebaseAuthManager
import com.bakudapa.adventure.data.remote.firebase.FirestoreManager
import com.bakudapa.adventure.feature.auth.domain.model.AuthUser
import com.bakudapa.adventure.feature.auth.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val authManager: FirebaseAuthManager,
    private val firestoreManager: FirestoreManager
) : AuthRepository {

    override val authUser: Flow<AuthUser?> = authManager.authStateFlow().map { user ->
        user?.let {
            AuthUser(
                uid = it.uid,
                email = it.email ?: "",
                displayName = it.displayName,
                photoUrl = it.photoUrl?.toString(),
                isEmailVerified = it.isEmailVerified
            )
        }
    }

    override suspend fun signIn(email: String, password: String): DataResult<AuthUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user!!
            DataResult.Success(
                AuthUser(
                    uid = user.uid,
                    email = user.email ?: "",
                    displayName = user.displayName,
                    photoUrl = user.photoUrl?.toString(),
                    isEmailVerified = user.isEmailVerified
                )
            )
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }

    override suspend fun signUp(email: String, password: String): DataResult<AuthUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user!!
            DataResult.Success(
                AuthUser(
                    uid = user.uid,
                    email = user.email ?: "",
                    isEmailVerified = user.isEmailVerified
                )
            )
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): DataResult<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }

    override suspend fun sendEmailVerification(): DataResult<Unit> {
        return try {
            auth.currentUser?.sendEmailVerification()?.await()
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }

    override suspend fun reloadUser(): DataResult<AuthUser?> {
        return try {
            auth.currentUser?.reload()?.await()
            val user = auth.currentUser
            DataResult.Success(
                user?.let {
                    AuthUser(
                        uid = it.uid,
                        email = it.email ?: "",
                        displayName = it.displayName,
                        photoUrl = it.photoUrl?.toString(),
                        isEmailVerified = it.isEmailVerified
                    )
                }
            )
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    override suspend fun completeProfile(displayName: String, photoUrl: String?): DataResult<Unit> {
        return try {
            val user = auth.currentUser ?: throw Exception("No authenticated user found")
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .apply {
                    photoUrl?.let { setPhotoUri(android.net.Uri.parse(it)) }
                }
                .build()
            
            user.updateProfile(profileUpdates).await()
            
            // Also save to firestore
            firestoreManager.getCollection("users").document(user.uid).set(
                mapOf(
                    "displayName" to displayName,
                    "photoUrl" to photoUrl,
                    "email" to user.email
                )
            ).await()
            
            DataResult.Success(Unit)
        } catch (e: Exception) {
            DataResult.Error(e)
        }
    }
}
