package com.bakudapa.adventure.feature.trail.domain.model

data class TrailReview(
    val id: String = "",
    val trailId: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val authorPhotoUrl: String? = null,
    val rating: Float = 0f,
    val comment: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
