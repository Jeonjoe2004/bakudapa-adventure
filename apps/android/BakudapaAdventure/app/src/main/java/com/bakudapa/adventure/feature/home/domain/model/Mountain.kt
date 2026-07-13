package com.bakudapa.adventure.feature.home.domain.model

data class Mountain(
    val id: String,
    val name: String,
    val location: String,
    val elevation: Int,
    val imageUrl: String,
    val rating: Float,
    val distance: Double? = null
)
