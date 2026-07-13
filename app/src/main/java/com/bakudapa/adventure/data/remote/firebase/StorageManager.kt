package com.bakudapa.adventure.data.remote.firebase

import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager for Cloud Storage operations.
 */
@Singleton
class StorageManager @Inject constructor(
    private val storage: FirebaseStorage
) {
    fun getReference(path: String): StorageReference = storage.getReference(path)
}
