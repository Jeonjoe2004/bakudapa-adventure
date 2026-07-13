package com.bakudapa.adventure.feature.feed.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.bakudapa.adventure.feature.feed.domain.model.Post
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PostItem(
    post: Post,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onSaveClick: () -> Unit,
    onShareClick: () -> Unit,
    onReportClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth().padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = rememberAsyncImagePainter(post.authorPhotoUrl),
                    contentDescription = null,
                    modifier = Modifier.size(40.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = post.authorName, fontWeight = FontWeight.Bold)
                    Text(
                        text = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(Date(post.timestamp)),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                IconButton(onClick = onReportClick) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Report")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Content
            if (post.content.isNotBlank()) {
                Text(text = post.content)
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Media
            post.mediaUrl?.let {
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onLikeClick) {
                            Icon(
                                if (post.isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Like",
                                tint = if (post.isLiked) MaterialTheme.colorScheme.error else LocalContentColor.current
                            )
                        }
                        Text(text = post.likesCount.toString())
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onCommentClick) {
                            Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = "Comment")
                        }
                        Text(text = post.commentsCount.toString())
                    }
                    IconButton(onClick = onShareClick) {
                        Icon(Icons.Outlined.Share, contentDescription = "Share")
                    }
                }
                IconButton(onClick = onSaveClick) {
                    Icon(
                        if (post.isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Save",
                        tint = if (post.isSaved) MaterialTheme.colorScheme.primary else LocalContentColor.current
                    )
                }
            }
        }
    }
}
