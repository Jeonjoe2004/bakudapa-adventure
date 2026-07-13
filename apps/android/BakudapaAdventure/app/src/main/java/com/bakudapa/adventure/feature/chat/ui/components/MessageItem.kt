package com.bakudapa.adventure.feature.chat.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bakudapa.adventure.feature.chat.domain.model.Message
import com.bakudapa.adventure.feature.chat.domain.model.MessageStatus
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MessageItem(
    message: Message,
    isMine: Boolean,
    modifier: Modifier = Modifier
) {
    val alignment = if (isMine) Alignment.CenterEnd else Alignment.CenterStart
    val backgroundColor = if (isMine) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (isMine) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    val shape = if (isMine) {
        RoundedCornerShape(12.dp, 12.dp, 0.dp, 12.dp)
    } else {
        RoundedCornerShape(12.dp, 12.dp, 12.dp, 0.dp)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = alignment
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(shape)
                .background(backgroundColor)
                .padding(8.dp)
        ) {
            if (!isMine) {
                Text(
                    text = message.senderName,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            }
            
            Text(text = message.content, color = textColor)
            
            Row(
                modifier = Modifier.align(Alignment.End),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.timestamp)),
                    fontSize = 10.sp,
                    color = textColor.copy(alpha = 0.7f)
                )
                if (isMine) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = if (message.status == MessageStatus.READ) Icons.Default.DoneAll else Icons.Default.Done,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = if (message.status == MessageStatus.READ) Color(0xFF00B0FF) else textColor.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}
