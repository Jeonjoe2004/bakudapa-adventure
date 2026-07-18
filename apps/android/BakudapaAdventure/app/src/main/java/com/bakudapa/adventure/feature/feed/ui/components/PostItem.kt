package com.bakudapa.adventure.feature.feed.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostItem(
    post: Post,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onSaveClick: () -> Unit,
    onShareClick: () -> Unit,
    onRepostClick: () -> Unit = {},
    onShareToChatClick: () -> Unit = {},
    onReportClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showShareSheet by remember { mutableStateOf(false) }

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
                                contentDescription = "Suka",
                                tint = if (post.isLiked) MaterialTheme.colorScheme.error else LocalContentColor.current
                            )
                        }
                        Text(text = post.likesCount.toString())
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onCommentClick) {
                            Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = "Komentar")
                        }
                        Text(text = post.commentsCount.toString())
                    }
                    IconButton(onClick = { showShareSheet = true }) {
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

    // IG-style Share Bottom Sheet
    if (showShareSheet) {
        ModalBottomSheet(
            onDismissRequest = { showShareSheet = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ShareOption(
                    icon = Icons.Default.Repeat,
                    label = "Bagikan ke Feed",
                    subtitle = "Posting ulang ke berandamu"
                ) {
                    showShareSheet = false
                    onRepostClick()
                }

                ShareOption(
                    icon = Icons.Default.Send,
                    label = "Kirim ke Chat/DM",
                    subtitle = "Bagikan ke teman"
                ) {
                    showShareSheet = false
                    onShareToChatClick()
                }

                HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp))

                ShareOption(
                    icon = Icons.Default.Share,
                    label = "Bagikan ke Aplikasi Lain",
                    subtitle = "WhatsApp, Telegram, dll"
                ) {
                    showShareSheet = false
                    onShareClick()
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ShareOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(28.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(Modifier.width(16.dp))
        Column {
            Text(label, fontWeight = FontWeight.SemiBold)
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}
