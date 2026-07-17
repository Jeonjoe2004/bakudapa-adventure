package com.bakudapa.adventure.feature.profile.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.bakudapa.adventure.feature.feed.domain.model.Post
import com.bakudapa.adventure.feature.feed.ui.components.PostItem
import com.bakudapa.adventure.feature.tracking.domain.model.HikingRoute
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToEditProfile: () -> Unit,
    onNavigateToAuth: () -> Unit,
    onNavigateToFollowers: () -> Unit = {},
    onNavigateToFollowing: () -> Unit = {},
    onNavigateToUserProfile: (String) -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var showSignOutDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                ProfileEffect.NavigateToEditProfile -> onNavigateToEditProfile()
                ProfileEffect.NavigateToAuth -> onNavigateToAuth()
                is ProfileEffect.ShowToast -> snackbarHostState.showSnackbar(effect.message)
                is ProfileEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
                ProfileEffect.NavigateToFollowers -> onNavigateToFollowers()
                ProfileEffect.NavigateToFollowing -> onNavigateToFollowing()
                is ProfileEffect.NavigateToUserProfile -> onNavigateToUserProfile(effect.userId)
            }
        }
    }

    // Sign out confirmation dialog
    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            icon = { Icon(Icons.Default.Logout, contentDescription = null) },
            title = { Text("Keluar?") },
            text = { Text("Kamu akan keluar dari akun ini.") },
            confirmButton = {
                Button(
                    onClick = {
                        showSignOutDialog = false
                        viewModel.onEvent(ProfileEvent.OnSignOutClicked)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Keluar") }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutDialog = false }) { Text("Batal") }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Profil") },
                actions = {
                    IconButton(onClick = { viewModel.onEvent(ProfileEvent.OnEditProfileClicked) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Profil")
                    }
                    IconButton(onClick = { showSignOutDialog = true }) {
                        Icon(Icons.Default.Logout, contentDescription = "Keluar")
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            // Search bar
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { viewModel.onEvent(ProfileEvent.OnSearchQueryChanged(it)) },
                placeholder = { Text("Cari pendaki...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(24.dp)
            )

            if (state.searchQuery.isNotBlank()) {
                // Search results
                if (state.isSearching) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (state.searchResults.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Tidak ditemukan", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
                    }
                } else {
                    LazyColumn {
                        items(state.searchResults) { user ->
                            ListItem(
                                headlineContent = { Text(user.name) },
                                leadingContent = {
                                    Icon(Icons.Default.Person, null, modifier = Modifier.size(40.dp))
                                },
                                modifier = Modifier.clickable {
                                    viewModel.onEvent(ProfileEvent.OnUserClicked(user.id))
                                }
                            )
                        }
                    }
                }
            } else {
                // Profile content (existing)
                if (state.isLoading && state.profile == null) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    LazyColumn {
            // ---- Header: Avatar + Nama + Level ----
            item {
                ProfileHeader(
                    photoUrl = state.profile?.photoUrl,
                    name = state.profile?.name ?: "Petualang",
                    username = state.profile?.username ?: "",
                    bio = state.profile?.bio ?: "",
                    website = state.profile?.website ?: "",
                    level = state.profile?.level ?: 1,
                    xp = state.profile?.xp ?: 0,
                    followersCount = state.followersCount,
                    followingCount = state.followingCount,
                    onFollowersClick = { viewModel.onEvent(ProfileEvent.OnFollowersClicked) },
                    onFollowingClick = { viewModel.onEvent(ProfileEvent.OnFollowingClicked) }
                )
            }

            // ---- Stats Cards ----
            item {
                ProfileStatsRow(
                    distanceKm = state.profile?.stats?.totalDistanceKm ?: 0.0,
                    elevationM = state.profile?.stats?.totalElevationM ?: 0,
                    hikesCount = state.profile?.stats?.mountainsClimbed ?: 0,
                    hoursTotal = state.profile?.stats?.totalHikingHours ?: 0.0
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // ---- Tabs ----
            item {
                TabRow(selectedTabIndex = state.selectedTab) {
                    Tab(
                        selected = state.selectedTab == 0,
                        onClick = { viewModel.onEvent(ProfileEvent.OnTabSelected(0)) },
                        text = { Text("Postingan (${state.myPosts.size})") }
                    )
                    Tab(
                        selected = state.selectedTab == 1,
                        onClick = { viewModel.onEvent(ProfileEvent.OnTabSelected(1)) },
                        text = { Text("Rute (${state.myRoutes.size})") }
                    )
                }
            }

            // ---- Tab Content ----
            if (state.selectedTab == 0) {
                if (state.myPosts.isEmpty()) {
                    item { EmptyTabContent(message = "Belum ada postingan", icon = Icons.Default.PhotoCamera) }
                } else {
                    items(state.myPosts, key = { it.id }) { post ->
                        PostItem(
                            post = post,
                            onLikeClick = {},
                            onCommentClick = {},
                            onSaveClick = {},
                            onShareClick = {},
                            onReportClick = {}
                        )
                    }
                }
            } else {
                if (state.myRoutes.isEmpty()) {
                    item { EmptyTabContent(message = "Belum ada rute tersimpan", icon = Icons.Default.Route) }
                } else {
                    items(state.myRoutes, key = { it.id }) { route ->
                        RouteItem(route = route)
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
            }
        }
    }
}

// ---- Sub-Composables ----

@Composable
private fun ProfileHeader(
    photoUrl: String?,
    name: String,
    username: String,
    bio: String,
    website: String,
    level: Int,
    xp: Int,
    followersCount: Int = 0,
    followingCount: Int = 0,
    onFollowersClick: () -> Unit = {},
    onFollowingClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar
        Box {
            Image(
                painter = rememberAsyncImagePainter(photoUrl),
                contentDescription = null,
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop
            )
            // Level badge
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(28.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "$level",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(text = name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "@${username.ifBlank { "username" }}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )

        // Bio
        if (bio.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = bio,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Website
        if (website.isNotBlank()) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = website,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Followers / Following (like Instagram)
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.clickable(enabled = false) {} // placeholder
        ) {
            TextButton(onClick = onFollowersClick) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("$followersCount") }
                        append("  pengikut")
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            TextButton(onClick = onFollowingClick) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("$followingCount") }
                        append("  mengikuti")
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // XP progress bar
        val xpForNextLevel = 1000
        val progress = (xp % xpForNextLevel) / xpForNextLevel.toFloat()
        Column(modifier = Modifier.fillMaxWidth(0.7f)) {
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${xp % xpForNextLevel} / $xpForNextLevel XP",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

@Composable
private fun ProfileStatsRow(
    distanceKm: Double,
    elevationM: Int,
    hikesCount: Int,
    hoursTotal: Double
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ProfileStatCard(
            icon = Icons.Default.Straighten,
            value = "%.1f".format(distanceKm),
            label = "km",
            modifier = Modifier.weight(1f)
        )
        ProfileStatCard(
            icon = Icons.Default.Terrain,
            value = "$elevationM",
            label = "m elevasi",
            modifier = Modifier.weight(1f)
        )
        ProfileStatCard(
            icon = Icons.Default.Hiking,
            value = "$hikesCount",
            label = "pendakian",
            modifier = Modifier.weight(1f)
        )
        ProfileStatCard(
            icon = Icons.Default.Schedule,
            value = "%.1f".format(hoursTotal),
            label = "jam",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ProfileStatCard(
    icon: ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
        }
    }
}

@Composable
private fun RouteItem(route: HikingRoute) {
    val distanceKm = "%.2f km".format(route.distanceMeters / 1000.0)
    val durationMin = "${route.durationMillis / 60_000} menit"
    val date = route.startTime.let {
        SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(it))
    }

    ListItem(
        headlineContent = {
            Text(
                text = route.name.ifBlank { "Rute tanpa nama" },
                fontWeight = FontWeight.SemiBold
            )
        },
        supportingContent = {
            Text(
                text = "$distanceKm  •  $durationMin  •  $date",
                color = MaterialTheme.colorScheme.outline
            )
        },
        leadingContent = {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Route,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        },
        trailingContent = {
            Text(
                text = "${route.calories} kkal",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    )
    HorizontalDivider(modifier = Modifier.padding(start = 72.dp))
}

@Composable
private fun EmptyTabContent(message: String, icon: ImageVector) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(56.dp),
            tint = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
    }
}
