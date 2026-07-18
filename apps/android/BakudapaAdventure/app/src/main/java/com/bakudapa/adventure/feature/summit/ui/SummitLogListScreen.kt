package com.bakudapa.adventure.feature.summit.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.bakudapa.adventure.feature.summit.domain.model.SummitLog
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SummitLogListSection(
    mountainId: String,
    viewModel: SummitLogListViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(mountainId) {
        viewModel.onEvent(SummitLogListEvent.LoadLogs(mountainId))
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            "Summit Logs (${state.logs.size})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        if (state.isLoading) {
            Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.logs.isEmpty()) {
            Text(
                "Belum ada summit log. Jadilah pendaki pertama yang check-in!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
        } else {
            state.logs.take(10).forEach { log ->
                SummitLogItem(log = log)
            }
            if (state.logs.size > 10) {
                TextButton(onClick = {}, modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text("Lihat semua (${state.logs.size})")
                }
            }
        }
    }
}

@Composable
private fun SummitLogItem(log: SummitLog) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = rememberAsyncImagePainter(log.userPhotoUrl),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp).clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(log.userName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                    Text(
                        SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(log.timestamp)),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            if (log.photoUrl != null) {
                Spacer(Modifier.height(8.dp))
                Image(
                    painter = rememberAsyncImagePainter(log.photoUrl),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().heightIn(max = 200.dp),
                    contentScale = ContentScale.Crop
                )
            }

            if (log.caption.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Text(log.caption, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
