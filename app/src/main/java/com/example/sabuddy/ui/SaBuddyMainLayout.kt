package com.example.sabuddy.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sabuddy.models.NavigationStyle
import com.example.sabuddy.models.SaScreen
import com.example.sabuddy.ui.screens.AnalyticsScreen
import com.example.sabuddy.ui.screens.BrandKitScreen
import com.example.sabuddy.ui.screens.CalendarScreen
import com.example.sabuddy.ui.screens.CaptionsScreen
import com.example.sabuddy.ui.screens.ChatScreen
import com.example.sabuddy.ui.screens.DashboardScreen
import com.example.sabuddy.ui.screens.HashtagsScreen
import com.example.sabuddy.ui.screens.IdeasScreen
import com.example.sabuddy.ui.screens.PostsScreen
import com.example.sabuddy.ui.screens.ReelsScreen
import com.example.sabuddy.ui.screens.SavedContentScreen
import com.example.sabuddy.ui.screens.SettingsScreen
import com.example.sabuddy.ui.screens.SetupScreen
import com.example.sabuddy.ui.theme.LocalSaBuddyColors
import com.example.sabuddy.ui.theme.SaBuddyTheme
import com.example.sabuddy.viewmodels.SaBuddyViewModel
import kotlinx.coroutines.launch

data class SaNavItem(
    val screen: SaScreen,
    val title: String,
    val icon: ImageVector
)

@Composable
fun SaBuddyApp(viewModel: SaBuddyViewModel) {
    val isSetupDone by viewModel.isSetupCompleted.collectAsStateWithLifecycle()
    val theme by viewModel.theme.collectAsStateWithLifecycle()
    val cardPreset by viewModel.cardPreset.collectAsStateWithLifecycle()
    val cornerRadius by viewModel.cornerRadius.collectAsStateWithLifecycle()
    val cardSpacing by viewModel.cardSpacing.collectAsStateWithLifecycle()
    val fontSize by viewModel.fontSize.collectAsStateWithLifecycle()

    SaBuddyTheme(
        selectedTheme = theme,
        cardPreset = cardPreset,
        customCornerRadius = cornerRadius,
        customCardSpacing = cardSpacing,
        fontSizePreference = fontSize
    ) {
        if (!isSetupDone) {
            SetupScreen(
                viewModel = viewModel,
                onSetupComplete = {
                    // Completed
                }
            )
        } else {
            SaBuddyMainScaffold(viewModel = viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SaBuddyMainScaffold(viewModel: SaBuddyViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val navStyle by viewModel.navStyle.collectAsStateWithLifecycle()
    val customColors = LocalSaBuddyColors.current

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navItems = listOf(
        SaNavItem(SaScreen.DASHBOARD, "Dashboard", Icons.Default.Dashboard),
        SaNavItem(SaScreen.CHAT, "SaBuddy AI", Icons.Default.AutoAwesome),
        SaNavItem(SaScreen.IDEAS, "Content Ideas", Icons.Default.Lightbulb),
        SaNavItem(SaScreen.REELS, "Reels Studio", Icons.Default.Movie),
        SaNavItem(SaScreen.POSTS, "Posts & Carousels", Icons.Default.PostAdd),
        SaNavItem(SaScreen.CAPTIONS, "Captions & Hooks", Icons.Default.FormatQuote),
        SaNavItem(SaScreen.HASHTAGS, "Hashtag Research", Icons.Default.Tag),
        SaNavItem(SaScreen.CALENDAR, "Content Calendar", Icons.Default.CalendarMonth),
        SaNavItem(SaScreen.SAVED_CONTENT, "Saved Content", Icons.Default.Bookmark),
        SaNavItem(SaScreen.ANALYTICS, "Analytics", Icons.Default.Analytics),
        SaNavItem(SaScreen.BRAND_KIT, "Brand Kit", Icons.Default.Palette),
        SaNavItem(SaScreen.SETTINGS, "Settings", Icons.Default.Settings)
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = customColors.cardBackground,
                drawerContentColor = MaterialTheme.colorScheme.onSurface
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "SaBuddy",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "AI Social Media Manager",
                            style = MaterialTheme.typography.bodySmall,
                            color = customColors.textMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                androidx.compose.foundation.lazy.LazyColumn(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)
                ) {
                    items(navItems.size) { index ->
                        val item = navItems[index]
                        val isSelected = currentScreen == item.screen

                        NavigationDrawerItem(
                            label = { Text(item.title, fontSize = 13.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                            icon = { Icon(item.icon, contentDescription = null, modifier = Modifier.size(18.dp)) },
                            selected = isSelected,
                            onClick = {
                                viewModel.navigateTo(item.screen)
                                scope.launch { drawerState.close() }
                            },
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                selectedIconColor = MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = when (currentScreen) {
                                SaScreen.DASHBOARD -> "SaBuddy"
                                SaScreen.CHAT -> "SaBuddy AI"
                                SaScreen.IDEAS -> "Content Ideas"
                                SaScreen.REELS -> "Reels Studio"
                                SaScreen.POSTS -> "Posts & Carousels"
                                SaScreen.CAPTIONS -> "Captions"
                                SaScreen.HASHTAGS -> "Hashtags"
                                SaScreen.CALENDAR -> "Content Calendar"
                                SaScreen.SAVED_CONTENT -> "Saved Content"
                                SaScreen.ANALYTICS -> "Analytics"
                                SaScreen.BRAND_KIT -> "Brand Kit"
                                SaScreen.SETTINGS -> "Settings"
                            },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.navigateTo(SaScreen.CHAT) }) {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search & Explore",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Box(
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            Color(0xFFD1E4FF),
                                            Color(0xFFAAC7FF)
                                        )
                                    )
                                )
                                .clickable { viewModel.navigateTo(SaScreen.BRAND_KIT) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "JD",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF003062)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            },
            bottomBar = {
                if (navStyle == NavigationStyle.FLOATING_NAV || navStyle == NavigationStyle.MODERN_SIDEBAR) {
                    val bottomItems = listOf(
                        SaNavItem(SaScreen.DASHBOARD, "Home", Icons.Default.Dashboard),
                        SaNavItem(SaScreen.CHAT, "Chat", Icons.Default.AutoAwesome),
                        SaNavItem(SaScreen.CALENDAR, "Calendar", Icons.Default.CalendarMonth),
                        SaNavItem(SaScreen.SAVED_CONTENT, "Saved", Icons.Default.Bookmark),
                        SaNavItem(SaScreen.SETTINGS, "Settings", Icons.Default.Settings)
                    )

                    NavigationBar(
                        containerColor = customColors.cardBackground,
                        tonalElevation = 0.dp,
                        modifier = Modifier.border(
                            width = 0.8.dp,
                            color = customColors.cardBorder
                        )
                    ) {
                        bottomItems.forEach { item ->
                            val isSelected = currentScreen == item.screen
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { viewModel.navigateTo(item.screen) },
                                icon = {
                                    Icon(
                                        item.icon,
                                        contentDescription = item.title,
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        item.title,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                                    selectedTextColor = MaterialTheme.colorScheme.onSurface,
                                    indicatorColor = MaterialTheme.colorScheme.primary,
                                    unselectedIconColor = customColors.textMuted,
                                    unselectedTextColor = customColors.textMuted
                                )
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Crossfade(targetState = currentScreen, label = "screen_transition") { screen ->
                    when (screen) {
                        SaScreen.DASHBOARD -> DashboardScreen(viewModel = viewModel)
                        SaScreen.CHAT -> ChatScreen(viewModel = viewModel)
                        SaScreen.IDEAS -> IdeasScreen(viewModel = viewModel)
                        SaScreen.REELS -> ReelsScreen(viewModel = viewModel)
                        SaScreen.POSTS -> PostsScreen(viewModel = viewModel)
                        SaScreen.CAPTIONS -> CaptionsScreen(viewModel = viewModel)
                        SaScreen.HASHTAGS -> HashtagsScreen(viewModel = viewModel)
                        SaScreen.CALENDAR -> CalendarScreen(viewModel = viewModel)
                        SaScreen.SAVED_CONTENT -> SavedContentScreen(viewModel = viewModel)
                        SaScreen.ANALYTICS -> AnalyticsScreen(viewModel = viewModel)
                        SaScreen.BRAND_KIT -> BrandKitScreen(viewModel = viewModel)
                        SaScreen.SETTINGS -> SettingsScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}
