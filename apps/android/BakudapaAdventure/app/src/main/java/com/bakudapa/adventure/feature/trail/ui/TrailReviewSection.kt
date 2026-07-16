package com.bakudapa.adventure.feature.trail.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bakudapa.adventure.feature.trail.domain.model.TrailReview
import java.text.SimpleDateFormat
import java.util.*

fun LazyListScope.TrailReviewSection(
    reviews: List<TrailReview>,
    reviewInput: String,
    reviewRating: Float,
    isSending: Boolean,
    onInputChanged: (String) -> Unit,
    onRatingChanged: (Float) -> Unit,
    onSend: () -> Unit
) {
    item {
        Text(
            text = "Reviews",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }

    // Input form
    item {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                RatingBar(
                    rating = reviewRating,
                    onRatingChanged = onRatingChanged,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = reviewInput,
                        onValueChange = onInputChanged,
                        placeholder = { Text("Write your review...") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 3,
                        enabled = !isSending
                    )
                    Spacer(Modifier.width(8.dp))
                    IconButton(
                        onClick = onSend,
                        enabled = reviewInput.isNotBlank() && !isSending
                    ) {
                        if (isSending) {
                            CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Send, contentDescription = "Send",
                                tint = if (reviewInput.isNotBlank()) MaterialTheme.colorScheme.primary
                                       else MaterialTheme.colorScheme.outline)
                        }
                    }
                }
            }
        }
    }

    if (reviews.isEmpty()) {
        item {
            Text(
                text = "No reviews yet. Be the first!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }
    } else {
        items(reviews.size) { i ->
            val review = reviews[i]
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(review.authorName, fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelMedium)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(2.dp))
                            Text("${review.rating}", style = MaterialTheme.typography.labelSmall)
                            Spacer(Modifier.width(8.dp))
                            Text(formatReviewTime(review.timestamp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline)
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(review.comment, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun RatingBar(
    rating: Float,
    onRatingChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        for (i in 1..5) {
            val star = if (i <= rating) Icons.Default.Star else Icons.Default.StarBorder
            IconButton(
                onClick = { onRatingChanged(i.toFloat()) },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    star,
                    contentDescription = "$i star",
                    tint = if (i <= rating) MaterialTheme.colorScheme.primary
                           else MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        if (rating > 0) {
            Text("${rating}/5", style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline)
        }
    }
}

private fun formatReviewTime(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    return when {
        diff < 60_000 -> "now"
        diff < 3_600_000 -> "${diff / 60_000}m ago"
        diff < 86_400_000 -> "${diff / 3_600_000}h ago"
        else -> SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(timestamp))
    }
}
