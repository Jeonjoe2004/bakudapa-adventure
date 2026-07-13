package com.bakudapa.adventure.feature.home.domain.model

data class HomeData(
    val recommendedMountains: List<Mountain>,
    val popularTrails: List<Trail>,
    val latestPosts: List<Post>,
    val nearbyMountains: List<Mountain>,
    val weather: Weather? = null
)
