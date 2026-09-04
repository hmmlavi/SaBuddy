package com.example.sabuddy.ui.screens

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sabuddy.data.CalendarEntryEntity
import com.example.sabuddy.data.ContentItemEntity
import com.example.sabuddy.data.PlatformProfileEntity
import com.example.sabuddy.models.CardConfig
import com.example.sabuddy.models.ContentCategory
import com.example.sabuddy.models.SaScreen
import com.example.sabuddy.ui.components.PlatformBadge
import com.example.sabuddy.ui.components.SaCard
import com.example.sabuddy.ui.components.SaSectionHeader
import com.example.sabuddy.ui.components.StatusBadge
import com.example.sabuddy.ui.components.copyToClipboard
import com.example.sabuddy.ui.theme.LocalCardSpacing
import com.example.sabuddy.ui.theme.LocalCornerRadius
import com.example.sabuddy.ui.theme.LocalSaBuddyColors
import com.example.sabuddy.viewmodels.SaBuddyViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(
    viewModel: SaBuddyViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val customColors = LocalSaBuddyColors.current
    val spacing = LocalCardSpacing.current
    val dashboardCards by viewModel.dashboardCards.collectAsStateWithLifecycle()
    val allContent by viewModel.allContentItems.collectAsStateWithLifecycle()
    val calendarEntries by viewModel.calendarEntries.collectAsStateWithLifecycle()
    val platforms by viewModel.platformProfiles.collectAsStateWithLifecycle()
    val recentActivities by viewModel.recentActivities.collectAsStateWithLifecycle()
    val analytics by viewModel.analytics.collectAsStateWithLifecycle()
    val isAiLoading by viewModel.isAiLoading.collectAsStateWithLifecycle()
    val aiStatusMessage by viewModel.aiStatusMessage.collectAsStateWithLifecycle()

    // Dialog state
    var selectedContentItem by remember { mutableStateOf<ContentItemEntity?>(null) }
    var selectedPlatformToEdit by remember { mutableStateOf<PlatformProfileEntity?>(null) }
    var showEditStatsDialog by remember { mutableStateOf(false) }
    var showQuickPromptDialog by remember { mutableStateOf<String?>(null) }

    // Today's date string
    val todayStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    val todayContentItem = remember(allContent, todayStr) {
        allContent.firstOrNull { it.scheduledDate == todayStr }
            ?: allContent.firstOrNull { it.title.contains("5 AI Tools", ignoreCase = true) }
            ?: allContent.firstOrNull()
    }

    val upcomingEntries = remember(calendarEntries, todayStr) {
        calendarEntries.filter { it.date >= todayStr }.take(4)
    }

    val recentIdeas = remember(allContent) {
        allContent.filter { it.category == "IDEAS" }.take(3)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(spacing)
    ) {
        item { Spacer(modifier = Modifier.height(6.dp)) }

        // Render cards according to user preference and order
        items(dashboardCards.filter { it.isVisible }, key = { it.id }) { cardConfig ->
            when (cardConfig.id) {
                "sabuddy_ai" -> {
                    Card1SaBuddyAi(
                        viewModel = viewModel,
                        onOpenChat = { viewModel.navigateTo(SaScreen.CHAT) },
                        onQuickSuggestion = { suggestion ->
                            when (suggestion) {
                                "Generate Ideas" -> viewModel.navigateTo(SaScreen.IDEAS)
                                "Create Reel" -> viewModel.navigateTo(SaScreen.REELS)
                                "Analyze Screenshot" -> {
                                    viewModel.sendChatMessage("Let's analyze my recent screenshot for hooks and visual structure.")
                                    viewModel.navigateTo(SaScreen.CHAT)
                                }
                            }
                        }
                    )
                }
                "today_content" -> {
                    Card2TodayContent(
                        contentItem = todayContentItem,
                        onOpenContent = { item -> selectedContentItem = item }
                    )
                }
                "upcoming_content" -> {
                    Card3UpcomingContent(
                        entries = upcomingEntries,
                        onViewCalendar = { viewModel.navigateTo(SaScreen.CALENDAR) },
                        onEntryClick = { entry ->
                            val linkedItem = allContent.firstOrNull { it.id == entry.contentItemId }
                                ?: ContentItemEntity(
                                    title = entry.title,
                                    platform = entry.platform,
                                    contentType = entry.contentType,
                                    status = entry.status,
                                    category = "REELS",
                                    scheduledDate = entry.date
                                )
                            selectedContentItem = linkedItem
                        }
                    )
                }
                "content_ideas" -> {
                    Card4ContentIdeas(
                        ideas = recentIdeas,
                        onViewAll = { viewModel.navigateTo(SaScreen.IDEAS) },
                        onIdeaClick = { idea -> selectedContentItem = idea }
                    )
                }
                "analytics" -> {
                    Card5Analytics(
                        analytics = analytics,
                        onUpdateStats = { showEditStatsDialog = true },
                        onViewFullAnalytics = { viewModel.navigateTo(SaScreen.ANALYTICS) }
                    )
                }
                "quick_actions" -> {
                    Card6QuickActions(
                        onAction = { action ->
                            when (action) {
                                "Generate Idea" -> viewModel.navigateTo(SaScreen.IDEAS)
                                "Create Reel" -> viewModel.navigateTo(SaScreen.REELS)
                                "Write Caption" -> viewModel.navigateTo(SaScreen.CAPTIONS)
                                "Generate Hashtags" -> viewModel.navigateTo(SaScreen.HASHTAGS)
                                "Analyze Screenshot" -> viewModel.navigateTo(SaScreen.CHAT)
                            }
                        }
                    )
                }
                "connected_platforms" -> {
                    Card7ConnectedPlatforms(
                        platforms = platforms,
                        onEditPlatform = { selectedPlatformToEdit = it }
                    )
                }
                "recent_activity" -> {
                    Card8RecentActivity(activities = recentActivities.take(5))
                }
                "saved_content" -> {
                    Card9SavedContent(
                        onSelectCategory = { category ->
                            viewModel.navigateTo(SaScreen.SAVED_CONTENT)
                        }
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }

    // Detail modal for content item
    if (selectedContentItem != null) {
        val item = selectedContentItem!!
        AlertDialog(
            onDismissRequest = { selectedContentItem = null },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    StatusBadge(status = item.status)
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PlatformBadge(platform = item.platform)
                        Text(
                            text = "• ${item.contentType}",
                            style = MaterialTheme.typography.bodySmall,
                            color = customColors.textMuted
                        )
                        if (item.scheduledDate != null) {
                            Text(
                                text = "• ${item.scheduledDate}",
                                style = MaterialTheme.typography.bodySmall,
                                color = customColors.textMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (item.hook.isNotBlank()) {
                        Text(
                            text = "HOOK",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "\"${item.hook}\"",
                            style = MaterialTheme.typography.bodyMedium,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    if (item.script.isNotBlank()) {
                        Text(
                            text = "SCRIPT",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(text = item.script, style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    if (item.body.isNotBlank()) {
                        Text(
                            text = "BODY / OUTLINE",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(text = item.body, style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    if (item.caption.isNotBlank()) {
                        Text(
                            text = "CAPTION",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(text = item.caption, style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    if (item.cta.isNotBlank()) {
                        Text(
                            text = "CTA",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF59E0B)
                        )
                        Text(text = item.cta, style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    if (item.hashtags.isNotBlank()) {
                        Text(
                            text = item.hashtags,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val fullText = buildString {
                            appendLine(item.title)
                            if (item.hook.isNotBlank()) appendLine("Hook: ${item.hook}")
                            if (item.script.isNotBlank()) appendLine("Script:\n${item.script}")
                            if (item.caption.isNotBlank()) appendLine("Caption:\n${item.caption}")
                            if (item.hashtags.isNotBlank()) appendLine(item.hashtags)
                        }
                        copyToClipboard(context, fullText, "Copied content details")
                    }
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy All")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedContentItem = null }) {
                    Text("Close")
                }
            }
        )
    }

    // Platform Profile Edit Dialog
    if (selectedPlatformToEdit != null) {
        val platform = selectedPlatformToEdit!!
        var username by remember { mutableStateOf(platform.username) }
        var profileUrl by remember { mutableStateOf(platform.profileUrl) }
        var isConnected by remember { mutableStateOf(platform.isConnected) }

        AlertDialog(
            onDismissRequest = { selectedPlatformToEdit = null },
            title = { Text("Edit ${platform.platform}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Connected Status", style = MaterialTheme.typography.bodyMedium)
                        androidx.compose.material3.Switch(
                            checked = isConnected,
                            onCheckedChange = { isConnected = it }
                        )
                    }
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username / Handle") },
                        placeholder = { Text("@yourhandle") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = profileUrl,
                        onValueChange = { profileUrl = it },
                        label = { Text("Profile URL") },
                        placeholder = { Text("https://${platform.platform.lowercase()}.com/...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updatePlatformProfile(
                            platform.copy(
                                isConnected = isConnected,
                                username = username.trim(),
                                profileUrl = profileUrl.trim()
                            )
                        )
                        selectedPlatformToEdit = null
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedPlatformToEdit = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Edit Analytics Dialog
    if (showEditStatsDialog) {
        var followersInput by remember { mutableStateOf((analytics?.followers ?: 24850).toString()) }
        var viewsInput by remember { mutableStateOf((analytics?.views ?: 182400).toString()) }
        var reachInput by remember { mutableStateOf((analytics?.reach ?: 95200).toString()) }
        var engagementInput by remember { mutableStateOf((analytics?.engagementRate ?: 6.4f).toString()) }

        AlertDialog(
            onDismissRequest = { showEditStatsDialog = false },
            title = { Text("Update Analytics Metrics") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = followersInput,
                        onValueChange = { followersInput = it },
                        label = { Text("Followers") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = viewsInput,
                        onValueChange = { viewsInput = it },
                        label = { Text("30-Day Views") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = reachInput,
                        onValueChange = { reachInput = it },
                        label = { Text("30-Day Reach") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = engagementInput,
                        onValueChange = { engagementInput = it },
                        label = { Text("Engagement Rate (%)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val f = followersInput.toLongOrNull() ?: (analytics?.followers ?: 0)
                        val v = viewsInput.toLongOrNull() ?: (analytics?.views ?: 0)
                        val r = reachInput.toLongOrNull() ?: (analytics?.reach ?: 0)
                        val e = engagementInput.toFloatOrNull() ?: (analytics?.engagementRate ?: 0f)
                        viewModel.updateAnalytics(f, v, r, e)
                        showEditStatsDialog = false
                    }
                ) {
                    Text("Update")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditStatsDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// ---------------------- 1. SaBuddy AI Card ----------------------
@Composable
private fun Card1SaBuddyAi(
    viewModel: SaBuddyViewModel,
    onOpenChat: () -> Unit,
    onQuickSuggestion: (String) -> Unit
) {
    val customColors = LocalSaBuddyColors.current
    val brandKit by viewModel.brandKit.collectAsStateWithLifecycle()
    val name = if (brandKit.brandName.isNotBlank()) brandKit.brandName else "Jane"

    Card(
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = customColors.heroCardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, customColors.heroCardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Background subtle decorative motif
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.04f),
                modifier = Modifier
                    .size(140.dp)
                    .align(Alignment.TopEnd)
            )

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "SABUDDY AI",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Hey $name, ready to\nscale your reach?",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 28.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onOpenChat,
                        shape = RoundedCornerShape(100.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Text("Open Chat", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Button(
                        onClick = { onQuickSuggestion("Generate Ideas") },
                        shape = RoundedCornerShape(100.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0x6644474E),
                            contentColor = Color.White
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF44474E)),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Text("New Idea", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val pills = listOf(
                        Triple("Analyze Screenshot", Color(0xFF4ADE80), "Analyze Screenshot"),
                        Triple("Viral Hook", Color(0xFF60A5FA), "Create Reel"),
                        Triple("Script Generator", Color(0xFFC084FC), "Generate Ideas"),
                        Triple("Caption & Tags", Color(0xFFFBBF24), "Generate Ideas")
                    )
                    items(pills) { (title, dotColor, action) ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0x66111318),
                            border = androidx.compose.foundation.BorderStroke(0.8.dp, Color(0x6644474E)),
                            modifier = Modifier.clickable { onQuickSuggestion(action) }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .background(dotColor, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(7.dp))
                                Text(
                                    text = title,
                                    fontSize = 11.5.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---------------------- 2. Today's Content Card ----------------------
@Composable
private fun Card2TodayContent(
    contentItem: ContentItemEntity?,
    onOpenContent: (ContentItemEntity) -> Unit
) {
    val customColors = LocalSaBuddyColors.current

    SaCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = { if (contentItem != null) onOpenContent(contentItem) }
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(customColors.iconContainerColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (contentItem?.contentType?.lowercase()) {
                                "reel" -> Icons.Default.Movie
                                "story" -> Icons.Default.Image
                                else -> Icons.Default.PostAdd
                            },
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Today's Content",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                StatusBadge(status = contentItem?.status ?: "Ready")
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (contentItem != null) {
                Text(
                    text = "${contentItem.platform} • ${contentItem.contentType}".uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = customColors.textMuted,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    fontSize = 10.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = contentItem.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 22.sp
                )

                if (contentItem.hook.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "\"${contentItem.hook}\"",
                        style = MaterialTheme.typography.bodySmall,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        color = customColors.textMuted,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "Tap to open content →",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                Text(
                    text = "No content scheduled for today. Generate an idea or script to get started!",
                    style = MaterialTheme.typography.bodySmall,
                    color = customColors.textMuted
                )
            }
        }
    }
}

// ---------------------- 3. Upcoming Content Card ----------------------
@Composable
private fun Card3UpcomingContent(
    entries: List<CalendarEntryEntity>,
    onViewCalendar: () -> Unit,
    onEntryClick: (CalendarEntryEntity) -> Unit
) {
    val customColors = LocalSaBuddyColors.current

    SaCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            SaSectionHeader(
                title = "Upcoming Content",
                actionText = "Calendar →",
                onActionClick = onViewCalendar
            )

            Spacer(modifier = Modifier.height(6.dp))

            if (entries.isEmpty()) {
                Text(
                    text = "No upcoming posts planned yet.",
                    style = MaterialTheme.typography.bodySmall,
                    color = customColors.textMuted
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    entries.forEach { entry ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { onEntryClick(entry) }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = entry.date,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = customColors.textMuted,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 10.sp
                                    )
                                    PlatformBadge(platform = entry.platform)
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = entry.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            StatusBadge(status = entry.status)
                        }
                    }
                }
            }
        }
    }
}

// ---------------------- 4. Content Ideas Card ----------------------
@Composable
private fun Card4ContentIdeas(
    ideas: List<ContentItemEntity>,
    onViewAll: () -> Unit,
    onIdeaClick: (ContentItemEntity) -> Unit
) {
    val customColors = LocalSaBuddyColors.current

    SaCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            SaSectionHeader(
                title = "Content Ideas",
                actionText = "View All →",
                onActionClick = onViewAll
            )

            Spacer(modifier = Modifier.height(6.dp))

            if (ideas.isEmpty()) {
                Text(
                    text = "No saved ideas yet. Click 'Generate Idea' below!",
                    style = MaterialTheme.typography.bodySmall,
                    color = customColors.textMuted
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    ideas.forEach { idea ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { onIdeaClick(idea) }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = Color(0xFFF59E0B),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = idea.title,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            PlatformBadge(platform = idea.platform)
                        }
                    }
                }
            }
        }
    }
}

// ---------------------- 5. Analytics Card ----------------------
@Composable
private fun Card5Analytics(
    analytics: com.example.sabuddy.data.AnalyticsEntity?,
    onUpdateStats: () -> Unit,
    onViewFullAnalytics: () -> Unit
) {
    val customColors = LocalSaBuddyColors.current
    val followers = analytics?.followers ?: 24850L
    val views = analytics?.views ?: 182400L
    val reach = analytics?.reach ?: 95200L
    val engagement = analytics?.engagementRate ?: 6.4f

    SaCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            SaSectionHeader(
                title = "Analytics",
                actionText = "Full Stats →",
                onActionClick = onViewFullAnalytics
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Highlight bar from the design
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = formatNumber(reach),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = analytics?.reachGrowth ?: "+8%",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4ADE80),
                        fontSize = 11.sp
                    )
                }
                Text(
                    text = "Reach this week",
                    style = MaterialTheme.typography.labelSmall,
                    color = customColors.textMuted,
                    fontSize = 10.5.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(Color(0x3344474E))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.70f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(100.dp))
                        .background(MaterialTheme.colorScheme.primary)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricTile(
                    modifier = Modifier.weight(1f),
                    label = "Followers",
                    value = formatNumber(followers),
                    growth = analytics?.followersGrowth ?: "+12.4%"
                )
                MetricTile(
                    modifier = Modifier.weight(1f),
                    label = "Views",
                    value = formatNumber(views),
                    growth = analytics?.viewsGrowth ?: "+18.2%"
                )
                MetricTile(
                    modifier = Modifier.weight(1f),
                    label = "Reach",
                    value = formatNumber(reach),
                    growth = analytics?.reachGrowth ?: "+9.8%"
                )
                MetricTile(
                    modifier = Modifier.weight(1f),
                    label = "Engage",
                    value = "$engagement%",
                    growth = analytics?.engagementGrowth ?: "+1.5%"
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onUpdateStats,
                    modifier = Modifier.height(28.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit Numbers", fontSize = 10.5.sp)
                }
            }
        }
    }
}

@Composable
private fun MetricTile(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    growth: String
) {
    val customColors = LocalSaBuddyColors.current
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = customColors.tagBackground,
        border = androidx.compose.foundation.BorderStroke(0.8.dp, customColors.cardBorder)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = customColors.textMuted,
                fontSize = 10.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = growth,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF4ADE80),
                fontSize = 9.sp
            )
        }
    }
}

// ---------------------- 6. Quick Actions Card ----------------------
@Composable
private fun Card6QuickActions(
    onAction: (String) -> Unit
) {
    val customColors = LocalSaBuddyColors.current
    val actions = listOf(
        Pair("Ideas", Icons.Default.Lightbulb),
        Pair("Reel", Icons.Default.Movie),
        Pair("Caption", Icons.Default.Edit),
        Pair("Tags", Icons.Default.Tag),
        Pair("Analyze", Icons.Default.Image)
    )

    SaCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            SaSectionHeader(
                title = "Quick Actions",
                actionText = "View All",
                onActionClick = { onAction("Generate Idea") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                actions.forEach { (label, icon) ->
                    val fullActionName = when (label) {
                        "Ideas" -> "Generate Idea"
                        "Reel" -> "Create Reel"
                        "Caption" -> "Write Caption"
                        "Tags" -> "Generate Hashtags"
                        "Analyze" -> "Analyze Screenshot"
                        else -> label
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { onAction(fullActionName) }
                            .padding(horizontal = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(customColors.tagBackground)
                                .border(
                                    width = 0.8.dp,
                                    color = customColors.cardBorder,
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

// ---------------------- 7. Connected Platforms Card ----------------------
@Composable
private fun Card7ConnectedPlatforms(
    platforms: List<PlatformProfileEntity>,
    onEditPlatform: (PlatformProfileEntity) -> Unit
) {
    val customColors = LocalSaBuddyColors.current

    SaCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            SaSectionHeader(
                title = "Connected Platforms",
                subtitle = "Tap to edit handles and links"
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                platforms.forEach { profile ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onEditPlatform(profile) }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            PlatformBadge(platform = profile.platform)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (profile.username.isNotBlank()) profile.username else "Not configured",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (profile.username.isNotBlank()) MaterialTheme.colorScheme.onSurface else customColors.textMuted,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (profile.isConnected) Color(0xFF065F46).copy(alpha = 0.2f) else customColors.tagBackground,
                            border = androidx.compose.foundation.BorderStroke(
                                0.5.dp,
                                if (profile.isConnected) Color(0xFF10B981) else customColors.cardBorder
                            )
                        ) {
                            Text(
                                text = if (profile.isConnected) "Connected" else "Not Connected",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (profile.isConnected) Color(0xFF10B981) else customColors.textMuted,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

// ---------------------- 8. Recent Activity Card ----------------------
@Composable
private fun Card8RecentActivity(
    activities: List<com.example.sabuddy.data.ActivityLogEntity>
) {
    val customColors = LocalSaBuddyColors.current

    SaCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    tint = customColors.textMuted,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Recent Activity",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (activities.isEmpty()) {
                Text(
                    text = "No recent actions yet.",
                    style = MaterialTheme.typography.bodySmall,
                    color = customColors.textMuted
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    activities.forEach { log ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = log.action,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = log.details,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = customColors.textMuted,
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---------------------- 9. Saved Content Card ----------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Card9SavedContent(
    onSelectCategory: (String) -> Unit
) {
    val categories = listOf("Ideas", "Reels", "Posts", "Captions", "Hooks", "Hashtags")
    val customColors = LocalSaBuddyColors.current

    SaCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Bookmark,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Saved Content",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "View All →",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onSelectCategory("All") }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = customColors.tagBackground,
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, customColors.cardBorder),
                        modifier = Modifier.clickable { onSelectCategory(category) }
                    ) {
                        Text(
                            text = category,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun formatNumber(num: Long): String {
    return when {
        num >= 1_000_000 -> String.format(Locale.US, "%.1fM", num / 1_000_000.0)
        num >= 1_000 -> String.format(Locale.US, "%.1fK", num / 1_000.0)
        else -> num.toString()
    }
}
