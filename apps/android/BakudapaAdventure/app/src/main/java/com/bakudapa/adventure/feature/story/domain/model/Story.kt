package com.bakudapa.adventure.feature.story.domain.model

data class Story(
    val id: String = "",
    val userId: String = "",
    val username: String = "",
    val userPhotoUrl: String? = null,
    val mediaUrl: String = "",
    val mediaType: StoryMediaType = StoryMediaType.PHOTO,
    val caption: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val expiresAt: Long = System.currentTimeMillis() + 86_400_000L, // 24 jam
    val viewerCount: Int = 0,
    val hasViewed: Boolean = false
)

enum class StoryMediaType {
    PHOTO, VIDEO
}

data class StoryUser(
    val userId: String = "",
    val username: String = "",
    val photoUrl: String? = null,
    val latestStory: Story? = null,
    val hasUnviewedStory: Boolean = false
)
