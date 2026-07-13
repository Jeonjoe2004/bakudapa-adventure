package com.bakudapa.adventure.feature.feed.domain.model

data class Post(
    val id: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val authorPhotoUrl: String? = null,
    val content: String = "",
    val mediaUrl: String? = null,
    val mediaType: MediaType? = null,
    val hashtags: List<String> = emptyList(),
    val mentions: List<String> = emptyList(),
    val likesCount: Int = 0,
    val commentsCount: Int = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val isLiked: Boolean = false,
    val isSaved: Boolean = false
)

enum class MediaType {
    PHOTO, VIDEO
}
