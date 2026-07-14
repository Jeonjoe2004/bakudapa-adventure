package com.bakudapa.adventure.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RssFeed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bakudapa.adventure.core.network.NetworkMonitor
import com.bakudapa.adventure.core.ui.components.OfflineOverlay
import com.bakudapa.adventure.navigation.NavGraph
import com.bakudapa.adventure.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    networkMonitor: NetworkMonitor
) : ViewModel() {
    val isOnline = networkMonitor.isOnline
}

@Composable
fun MainContainer(
    navController: NavHostController = rememberNavController(),
    auth: FirebaseAuth,
    viewModel: MainViewModel = hiltViewModel()
) {
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle(initialValue = true)
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    val bottomBarScreens = listOf(
        Screen.Home,
        Screen.Map,
        Screen.Feed,
        Screen.Chat,
        Screen.Profile
    )
    
    val showBottomBar = bottomBarScreens.any { screen -> 
        currentDestination?.hierarchy?.any { it.route == screen.route } == true 
    }

    Scaffold(
        bottomBar = {
            Column {
                if (showBottomBar) {
                    NavigationBar {
                        bottomBarItems.forEach { item ->
                            val selected = currentDestination?.hierarchy?.any { it.route == item.screen.route } == true
                            NavigationBarItem(
                                icon = { Icon(item.icon, contentDescription = item.label) },
                                label = { Text(item.label) },
                                selected = selected,
                                onClick = {
                                    navController.navigate(item.screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                }
                OfflineOverlay(isOnline = isOnline)
            }
        }
    ) { innerPadding ->
        NavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding),
            auth = auth
        )
    }
}

data class BottomBarItem(
    val screen: Screen,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

val bottomBarItems = listOf(
    BottomBarItem(Screen.Home, "Home", Icons.Default.Home),
    BottomBarItem(Screen.Map, "Map", Icons.Default.Map),
    BottomBarItem(Screen.Feed, "Feed", Icons.Default.RssFeed),
    BottomBarItem(Screen.Chat, "Chat", Icons.Default.Chat),
    BottomBarItem(Screen.Profile, "Profile", Icons.Default.Person)
)
