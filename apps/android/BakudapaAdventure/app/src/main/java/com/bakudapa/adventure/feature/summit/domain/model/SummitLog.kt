package com.bakudapa.adventure.feature.summit.domain.model

data class SummitLog(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userPhotoUrl: String? = null,
    val mountainId: String = "",
    val mountainName: String = "",
    val photoUrl: String? = null,
    val caption: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val elevation: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)
