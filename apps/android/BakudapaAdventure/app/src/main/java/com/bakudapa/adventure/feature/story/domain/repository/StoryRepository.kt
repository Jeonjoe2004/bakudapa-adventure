package com.bakudapa.adventure.feature.story.domain.repository

import android.net.Uri
import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.story.domain.model.Story
import com.bakudapa.adventure.feature.story.domain.model.StoryUser
import kotlinx.coroutines.flow.Flow

interface StoryRepository {
    /** Ambil semua story yang belum expired, grup per user */
    fun getActiveStories(): Flow<DataResult<List<StoryUser>>>

    /** Ambil story dari user tertentu */
    fun getUserStories(userId: String): Flow<DataResult<List<Story>>>

    /** Upload story baru */
    suspend fun createStory(mediaUri: Uri, caption: String): DataResult<Unit>

    /** Tandai story sudah dilihat */
    suspend fun markViewed(storyId: String): DataResult<Unit>

    /** Hapus story (owner only) */
    suspend fun deleteStory(storyId: String): DataResult<Unit>
}
