package com.bakudapa.adventure.feature.badge.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BadgeScreen(
    onNavigateBack: () -> Unit,
    viewModel: BadgeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Achievements") })
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Text(
                text = "Earn badges by exploring more mountains!",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.outline
            )
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                items(state.allBadges) { badge ->
                    val isUnlocked = state.myBadges.any { it.id == badge.id }
                    BadgeItem(
                        badge = badge,
                        isUnlocked = isUnlocked,
                        onClick = { viewModel.onEvent(BadgeEvent.OnBadgeClicked(badge)) }
                    )
                }
            }
        }
    }
}

@Composable
fun BadgeItem(
    badge: com.bakudapa.adventure.feature.badge.domain.model.Badge,
    isUnlocked: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(if (isUnlocked) MaterialTheme.colorScheme.primaryContainer else Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = rememberAsyncImagePainter(badge.imageUrl),
                contentDescription = badge.name,
                modifier = Modifier.size(60.dp),
                alpha = if (isUnlocked) 1f else 0.3f
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = badge.name,
            style = MaterialTheme.typography.labelSmall,
            textAlign = TextAlign.Center,
            fontWeight = if (isUnlocked) FontWeight.Bold else FontWeight.Normal,
            maxLines = 2
        )
    }
}
