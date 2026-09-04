package com.example.sabuddy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sabuddy.data.ContentItemEntity
import com.example.sabuddy.ui.components.PlatformBadge
import com.example.sabuddy.ui.components.SaCard
import com.example.sabuddy.ui.components.StatusBadge
import com.example.sabuddy.ui.components.copyToClipboard
import com.example.sabuddy.ui.theme.LocalSaBuddyColors
import com.example.sabuddy.viewmodels.SaBuddyViewModel

@Composable
fun ReelsScreen(
    viewModel: SaBuddyViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val customColors = LocalSaBuddyColors.current
    val allContent by viewModel.allContentItems.collectAsStateWithLifecycle()
    val isAiLoading by viewModel.isAiLoading.collectAsStateWithLifecycle()
    val aiStatusMessage by viewModel.aiStatusMessage.collectAsStateWithLifecycle()
    val lastGeneratedItem by viewModel.lastGeneratedItem.collectAsStateWithLifecycle()

    var selectedTabIndex by remember { mutableStateOf(0) } // 0: Studio / Generator, 1: Saved Reels

    // Reel Editor fields
    var reelTopic by remember { mutableStateOf("") }
    var reelTitle by remember { mutableStateOf("5 AI Tools You Should Know") }
    var reelHook by remember { mutableStateOf("Stop wasting 10 hours a week on manual editing. Here are 5 secret AI tools changing the game.") }
    var reelScript by remember { mutableStateOf("0:00 - 0:03 [Visual hook: Fast cut of frustrating timeline]\n0:03 - 0:15 Tool 1: AI voiceover studio\n0:15 - 0:30 Tool 2: Automatic caption highlighter\n0:30 - 0:45 Tool 3: Smart hook grader\n0:45 - 0:60 CTA: Comment 'TOOLS' to get the free Notion list!") }
    var reelCaption by remember { mutableStateOf("These 5 AI tools will easily 10x your creator workflow this month. Which one is your favorite? Drop a comment below! 👇") }
    var reelCta by remember { mutableStateOf("Comment 'TOOLS' to get the full free PDF guide!") }
    var reelHashtags by remember { mutableStateOf("#AITools #CreatorEconomy #SocialMediaTips #ReelsViral #ProductivityHacks") }

    // Update fields when an item is generated
    remember(lastGeneratedItem) {
        lastGeneratedItem?.let { item ->
            if (item.category == "REELS") {
                reelTitle = item.title
                reelHook = item.hook
                if (item.script.isNotBlank()) reelScript = item.script
                if (item.caption.isNotBlank()) reelCaption = item.caption
                if (item.cta.isNotBlank()) reelCta = item.cta
                if (item.hashtags.isNotBlank()) reelHashtags = item.hashtags
            }
        }
    }

    val savedReels = remember(allContent) {
        allContent.filter { it.category == "REELS" }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Reels Studio",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = customColors.cardBackground,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = { Text("Script Generator", fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = { Text("Saved Reels (${savedReels.size})", fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (selectedTabIndex == 0) {
            // Studio / Editor
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Topic generator box
                SaCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "AI Reel Topic & Idea",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        OutlinedTextField(
                            value = reelTopic,
                            onValueChange = { reelTopic = it },
                            placeholder = { Text("e.g. 3 psychology tricks to double your video watch time") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.generateReel(reelTopic) },
                                enabled = !isAiLoading,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                if (isAiLoading) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = MaterialTheme.colorScheme.onPrimary)
                                } else {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(15.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Generate Script", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Reel Details Card
                SaCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Reel Content Workspace",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        OutlinedTextField(
                            value = reelTitle,
                            onValueChange = { reelTitle = it },
                            label = { Text("Title") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = reelHook,
                            onValueChange = { reelHook = it },
                            label = { Text("Hook (First 3 seconds)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = reelScript,
                            onValueChange = { reelScript = it },
                            label = { Text("Script Breakdown") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 4
                        )

                        OutlinedTextField(
                            value = reelCaption,
                            onValueChange = { reelCaption = it },
                            label = { Text("Caption") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )

                        OutlinedTextField(
                            value = reelCta,
                            onValueChange = { reelCta = it },
                            label = { Text("Call to Action (CTA)") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = reelHashtags,
                            onValueChange = { reelHashtags = it },
                            label = { Text("Hashtags") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Action Buttons: Save, Copy, Add to Calendar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    val item = ContentItemEntity(
                                        category = "REELS",
                                        title = reelTitle,
                                        hook = reelHook,
                                        script = reelScript,
                                        caption = reelCaption,
                                        cta = reelCta,
                                        hashtags = reelHashtags,
                                        platform = "Instagram",
                                        contentType = "Reel",
                                        status = "Ready"
                                    )
                                    viewModel.saveContentItem(item)
                                    copyToClipboard(context, "", "Reel saved to library!")
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Outlined.BookmarkBorder, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Save Reel", fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    val fullScript = """
                                        TITLE: $reelTitle
                                        HOOK: $reelHook
                                        
                                        SCRIPT:
                                        $reelScript
                                        
                                        CAPTION:
                                        $reelCaption
                                        
                                        CTA: $reelCta
                                        $reelHashtags
                                    """.trimIndent()
                                    copyToClipboard(context, fullScript, "Full Reel script copied!")
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Copy Script", fontSize = 12.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        } else {
            // Saved Reels List
            if (savedReels.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No saved reels yet.", color = customColors.textMuted)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(savedReels, key = { it.id }) { reel ->
                        SaCard(modifier = Modifier.fillMaxWidth()) {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    PlatformBadge(platform = reel.platform)
                                    StatusBadge(status = reel.status)
                                }
                                Text(
                                    text = reel.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                if (reel.hook.isNotBlank()) {
                                    Text(
                                        text = "\"${reel.hook}\"",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Text(
                                    text = reel.script,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = customColors.textMuted,
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    IconButton(
                                        onClick = {
                                            copyToClipboard(context, "${reel.title}\n${reel.hook}\n\n${reel.script}", "Copied!")
                                        }
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = customColors.textMuted, modifier = Modifier.size(16.dp))
                                    }
                                    IconButton(onClick = { viewModel.addContentToCalendar(reel) }) {
                                        Icon(Icons.Default.CalendarMonth, contentDescription = "Calendar", tint = customColors.textMuted, modifier = Modifier.size(16.dp))
                                    }
                                    IconButton(onClick = { viewModel.deleteContentItem(reel) }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }
        }
    }
}
