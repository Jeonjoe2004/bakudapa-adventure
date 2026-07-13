package com.bakudapa.adventure.feature.home.domain.model

data class Post(
    val id: String,
    val authorName: String,
    val authorImageUrl: String,
    val content: String,
    val imageUrl: String? = null,
    val timestamp: Long,
    val likesCount: Int,
    val commentsCount: Int
)
