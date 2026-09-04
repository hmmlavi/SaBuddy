package com.example.sabuddy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sabuddy.data.ContentItemEntity
import com.example.sabuddy.models.HashtagGroups
import com.example.sabuddy.ui.components.SaCard
import com.example.sabuddy.ui.components.copyToClipboard
import com.example.sabuddy.ui.theme.LocalSaBuddyColors
import com.example.sabuddy.viewmodels.SaBuddyViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HashtagsScreen(
    viewModel: SaBuddyViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val customColors = LocalSaBuddyColors.current
    val isAiLoading by viewModel.isAiLoading.collectAsStateWithLifecycle()
    val lastGeneratedTags by viewModel.lastGeneratedHashtags.collectAsStateWithLifecycle()

    var topic by remember { mutableStateOf("AI Productivity & Creator Tools") }
    var platform by remember { mutableStateOf("Instagram") }
    var countInput by remember { mutableStateOf("15") }

    val activeTags = lastGeneratedTags ?: HashtagGroups(
        highReach = listOf("#ProductivityHacks", "#CreatorEconomy", "#SocialMediaTips", "#TechTrends"),
        niche = listOf("#AICreatorHub", "#SoloCreatorTools", "#RetentionHacks", "#WorkflowAutomation"),
        relevant = listOf("#ContentStrategy2026", "#ReelsGrowthFormula", "#DigitalCreatorsClub", "#SmartWorkflows")
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Hashtag Research",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Input Box
        SaCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = topic,
                    onValueChange = { topic = it },
                    label = { Text("Topic or Keyword") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = platform,
                        onValueChange = { platform = it },
                        label = { Text("Platform") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = countInput,
                        onValueChange = { countInput = it },
                        label = { Text("Count") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Button(
                    onClick = {
                        val count = countInput.toIntOrNull() ?: 15
                        viewModel.generateHashtags(topic, platform, count)
                    },
                    enabled = !isAiLoading,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (isAiLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Generate Hashtags", fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Group 1: High Reach
        HashtagGroupCard(
            title = "High Reach (Macro)",
            subtitle = "Broad tags with high search volume",
            tags = activeTags.highReach,
            badgeColor = Color(0xFF38BDF8)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Group 2: Niche
        HashtagGroupCard(
            title = "Niche (Micro)",
            subtitle = "Specific tags with engaged communities",
            tags = activeTags.niche,
            badgeColor = Color(0xFF10B981)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Group 3: Relevant
        HashtagGroupCard(
            title = "Relevant (Topic Context)",
            subtitle = "Directly matching your current subject",
            tags = activeTags.relevant,
            badgeColor = Color(0xFFF59E0B)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Combined Actions: Copy All & Save
        val allTagsString = remember(activeTags) {
            (activeTags.highReach + activeTags.niche + activeTags.relevant).joinToString(" ")
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = { copyToClipboard(context, allTagsString, "All hashtags copied!") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Copy All Hashtags", fontSize = 12.sp)
            }

            OutlinedButton(
                onClick = {
                    val item = ContentItemEntity(
                        category = "HASHTAGS",
                        title = "Hashtags: $topic".take(40),
                        hashtags = allTagsString,
                        platform = platform,
                        contentType = "Hashtags"
                    )
                    viewModel.saveContentItem(item)
                    copyToClipboard(context, "", "Saved hashtags to library!")
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Outlined.BookmarkBorder, contentDescription = null, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Save Hashtags", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HashtagGroupCard(
    title: String,
    subtitle: String,
    tags: List<String>,
    badgeColor: Color
) {
    val customColors = LocalSaBuddyColors.current

    SaCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = customColors.textMuted,
                        fontSize = 11.sp
                    )
                }
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = badgeColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "${tags.size} tags",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                tags.forEach { tag ->
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = customColors.tagBackground,
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, customColors.cardBorder)
                    ) {
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
