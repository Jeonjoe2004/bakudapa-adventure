package com.bakudapa.adventure.feature.feed.domain.repository

import android.net.Uri
import com.bakudapa.adventure.core.data.DataResult
import com.bakudapa.adventure.feature.feed.domain.model.Comment
import com.bakudapa.adventure.feature.feed.domain.model.Post
import kotlinx.coroutines.flow.Flow

interface FeedRepository {
    fun getPosts(): Flow<DataResult<List<Post>>>
    
    suspend fun createPost(
        content: String,
        mediaUri: Uri?,
        hashtags: List<String>,
        mentions: List<String>
    ): DataResult<Unit>
    
    suspend fun likePost(postId: String): DataResult<Unit>
    
    suspend fun unlikePost(postId: String): DataResult<Unit>
    
    suspend fun savePost(postId: String): DataResult<Unit>
    
    suspend fun unsavePost(postId: String): DataResult<Unit>
    
    fun getComments(postId: String): Flow<DataResult<List<Comment>>>
    
    suspend fun addComment(postId: String, content: String): DataResult<Unit>
    
    suspend fun reportPost(postId: String, reason: String): DataResult<Unit>
}
