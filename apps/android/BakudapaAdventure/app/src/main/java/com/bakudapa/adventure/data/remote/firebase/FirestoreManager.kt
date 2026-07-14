package com.bakudapa.adventure.data.remote.firebase

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager for Cloud Firestore operations.
 */
@Singleton
class FirestoreManager @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    fun getCollection(path: String): CollectionReference = firestore.collection(path)

    fun getDocument(path: String): DocumentReference = firestore.document(path)

    fun getFirestore(): FirebaseFirestore = firestore
}
