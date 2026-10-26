package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.SocialRepository
import com.example.ui.theme.InstaPink

enum class ScreenTab {
    HOME,
    EXPLORE,
    CREATE,
    NOTIFICATIONS,
    PROFILE
}

@Composable
fun MainScreen(
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val isAuthenticated by SocialRepository.isAuthenticated.collectAsStateWithLifecycle()
    val notifications by SocialRepository.notifications.collectAsStateWithLifecycle()
    val hasUnreadNotifications = remember(notifications) {
        notifications.any { !it.isRead }
    }

    var currentTab by remember { mutableStateOf(ScreenTab.HOME) }
    var isInDirectMessages by remember { mutableStateOf(false) }

    if (!isAuthenticated) {
        AuthScreen(
            onLoginSuccess = { currentTab = ScreenTab.HOME }
        )
    } else if (isInDirectMessages) {
        DirectMessagesScreen(
            onNavigateBack = { isInDirectMessages = false }
        )
    } else {
        Scaffold(
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 4.dp,
                    modifier = Modifier
                        .navigationBarsPadding()
                        .testTag("main_bottom_nav")
                ) {
                    NavigationBarItem(
                        selected = currentTab == ScreenTab.HOME,
                        onClick = { currentTab = ScreenTab.HOME },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == ScreenTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                                contentDescription = "Home"
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = InstaPink,
                            indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_tab_home")
                    )

                    NavigationBarItem(
                        selected = currentTab == ScreenTab.EXPLORE,
                        onClick = { currentTab = ScreenTab.EXPLORE },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == ScreenTab.EXPLORE) Icons.Filled.Search else Icons.Outlined.Search,
                                contentDescription = "Explore"
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = InstaPink,
                            indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_tab_explore")
                    )

                    NavigationBarItem(
                        selected = currentTab == ScreenTab.CREATE,
                        onClick = { currentTab = ScreenTab.CREATE },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == ScreenTab.CREATE) Icons.Filled.AddBox else Icons.Outlined.AddBox,
                                contentDescription = "Create"
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = InstaPink,
                            indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_tab_create")
                    )

                    NavigationBarItem(
                        selected = currentTab == ScreenTab.NOTIFICATIONS,
                        onClick = { currentTab = ScreenTab.NOTIFICATIONS },
                        icon = {
                            BadgedBox(
                                badge = {
                                    if (hasUnreadNotifications) {
                                        Badge(containerColor = InstaPink)
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (currentTab == ScreenTab.NOTIFICATIONS) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                    contentDescription = "Notifications"
                                )
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = InstaPink,
                            indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_tab_notifications")
                    )

                    NavigationBarItem(
                        selected = currentTab == ScreenTab.PROFILE,
                        onClick = { currentTab = ScreenTab.PROFILE },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == ScreenTab.PROFILE) Icons.Filled.AccountCircle else Icons.Outlined.AccountCircle,
                                contentDescription = "Profile"
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = InstaPink,
                            indicatorColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.testTag("nav_tab_profile")
                    )
                }
            },
            modifier = modifier.fillMaxSize()
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentTab) {
                    ScreenTab.HOME -> {
                        HomeFeedScreen(
                            onNavigateToDMs = { isInDirectMessages = true },
                            isDarkMode = isDarkMode,
                            onToggleDarkMode = onToggleDarkMode
                        )
                    }
                    ScreenTab.EXPLORE -> {
                        ExploreScreen()
                    }
                    ScreenTab.CREATE -> {
                        CreatePostScreen(
                            onPostCreated = { currentTab = ScreenTab.HOME }
                        )
                    }
                    ScreenTab.NOTIFICATIONS -> {
                        NotificationsScreen()
                    }
                    ScreenTab.PROFILE -> {
                        ProfileScreen(
                            onLogout = {
                                com.example.data.FirebaseAuthHelper.signOut(context, coroutineScope) {
                                    currentTab = ScreenTab.HOME
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
