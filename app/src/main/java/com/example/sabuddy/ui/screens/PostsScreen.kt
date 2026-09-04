package com.example.sabuddy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
fun PostsScreen(
    viewModel: SaBuddyViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val customColors = LocalSaBuddyColors.current
    val allContent by viewModel.allContentItems.collectAsStateWithLifecycle()
    val isAiLoading by viewModel.isAiLoading.collectAsStateWithLifecycle()
    val lastGeneratedItem by viewModel.lastGeneratedItem.collectAsStateWithLifecycle()

    var selectedTabIndex by remember { mutableStateOf(0) } // 0: Generator, 1: Saved Posts

    // Post Type: Single Post, Carousel, Educational Post, Promotional Post
    val postTypes = listOf("Single Post", "Carousel", "Educational Post", "Promotional Post")
    var selectedPostType by remember { mutableStateOf("Carousel") }
    var selectedPlatform by remember { mutableStateOf("LinkedIn") }
    val platforms = listOf("LinkedIn", "Instagram", "X / Twitter", "Facebook")

    var topicInput by remember { mutableStateOf("") }
    var postTitle by remember { mutableStateOf("How to Hook Viewers in 3 Seconds") }
    var postHook by remember { mutableStateOf("Your first 3 seconds decide 90% of your video's retention.") }
    var postBody by remember { mutableStateOf("Slide 1: The opening trap\nSlide 2: Visual contrast before audio\nSlide 3: Posing high stakes questions\nSlide 4: The 3-second retention framework\nSlide 5: Actionable checklist") }
    var postCaption by remember { mutableStateOf("Stop losing viewers on the opening frame. Here is the 3-second retention framework top creators swear by 📈") }
    var postCta by remember { mutableStateOf("Save this carousel for your next content batch!") }
    var postHashtags by remember { mutableStateOf("#ContentStrategy #HookFormula #SocialMediaGrowth #CreatorTips") }

    remember(lastGeneratedItem) {
        lastGeneratedItem?.let { item ->
            if (item.category == "POSTS") {
                postTitle = item.title
                postHook = item.hook
                if (item.body.isNotBlank()) postBody = item.body
                if (item.caption.isNotBlank()) postCaption = item.caption
                if (item.cta.isNotBlank()) postCta = item.cta
                if (item.hashtags.isNotBlank()) postHashtags = item.hashtags
            }
        }
    }

    val savedPosts = remember(allContent) {
        allContent.filter { it.category == "POSTS" }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Posts & Carousels",
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
                text = { Text("Post Creator", fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
            )
            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = { Text("Saved Posts (${savedPosts.size})", fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (selectedTabIndex == 0) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Post Configuration Box
                SaCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Configure Post Type & Platform",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        // Post Type selector row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            postTypes.forEach { type ->
                                val isSelected = selectedPostType == type
                                OutlinedButton(
                                    onClick = { selectedPostType = type },
                                    modifier = Modifier.weight(1f).height(32.dp),
                                    shape = RoundedCornerShape(6.dp),
                                    colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(
                                        if (isSelected) 1.5.dp else 0.5.dp,
                                        if (isSelected) MaterialTheme.colorScheme.primary else customColors.cardBorder
                                    )
                                ) {
                                    Text(
                                        text = type.replace(" Post", ""),
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        maxLines = 1
                                    )
                                }
                            }
                        }

                        // Platform selector
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            platforms.forEach { p ->
                                val isSelected = selectedPlatform == p
                                OutlinedButton(
                                    onClick = { selectedPlatform = p },
                                    modifier = Modifier.weight(1f).height(30.dp),
                                    shape = RoundedCornerShape(6.dp),
                                    colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                                    )
                                ) {
                                    Text(text = p.split(" ")[0], fontSize = 10.5.sp, maxLines = 1)
                                }
                            }
                        }

                        OutlinedTextField(
                            value = topicInput,
                            onValueChange = { topicInput = it },
                            placeholder = { Text("Topic: e.g. 5 mindset shifts for creative flow") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )

                        Button(
                            onClick = {
                                viewModel.generatePost(
                                    topic = topicInput,
                                    postType = selectedPostType,
                                    platform = selectedPlatform
                                )
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
                                Text("Generate $selectedPostType", fontSize = 12.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Post Editor
                SaCard(modifier = Modifier.fillMaxWidth()) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Post Workspace",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        OutlinedTextField(
                            value = postTitle,
                            onValueChange = { postTitle = it },
                            label = { Text("Title") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = postHook,
                            onValueChange = { postHook = it },
                            label = { Text("Hook / Headline") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = postBody,
                            onValueChange = { postBody = it },
                            label = { Text("Body / Slides Outline") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 4
                        )

                        OutlinedTextField(
                            value = postCaption,
                            onValueChange = { postCaption = it },
                            label = { Text("Caption") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )

                        OutlinedTextField(
                            value = postCta,
                            onValueChange = { postCta = it },
                            label = { Text("CTA") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = postHashtags,
                            onValueChange = { postHashtags = it },
                            label = { Text("Hashtags") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    val item = ContentItemEntity(
                                        category = "POSTS",
                                        title = postTitle,
                                        hook = postHook,
                                        body = postBody,
                                        caption = postCaption,
                                        cta = postCta,
                                        hashtags = postHashtags,
                                        platform = selectedPlatform,
                                        contentType = selectedPostType,
                                        status = "Ready"
                                    )
                                    viewModel.saveContentItem(item)
                                    copyToClipboard(context, "", "Post saved to library!")
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Outlined.BookmarkBorder, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Save Post", fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    val full = """
                                        TITLE: $postTitle
                                        HOOK: $postHook
                                        
                                        $postBody
                                        
                                        CAPTION:
                                        $postCaption
                                        
                                        $postCta
                                        $postHashtags
                                    """.trimIndent()
                                    copyToClipboard(context, full, "Post copied to clipboard!")
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Copy Post", fontSize = 12.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        } else {
            // Saved Posts
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(savedPosts, key = { it.id }) { post ->
                    SaCard(modifier = Modifier.fillMaxWidth()) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                PlatformBadge(platform = post.platform)
                                StatusBadge(status = post.status)
                            }
                            Text(
                                text = post.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            if (post.hook.isNotBlank()) {
                                Text(
                                    text = "\"${post.hook}\"",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Text(
                                text = post.body.ifBlank { post.caption },
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
                                        copyToClipboard(context, "${post.title}\n\n${post.body}\n\n${post.caption}", "Copied!")
                                    }
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = customColors.textMuted, modifier = Modifier.size(16.dp))
                                }
                                IconButton(onClick = { viewModel.addContentToCalendar(post) }) {
                                    Icon(Icons.Default.CalendarMonth, contentDescription = "Calendar", tint = customColors.textMuted, modifier = Modifier.size(16.dp))
                                }
                                IconButton(onClick = { viewModel.deleteContentItem(post) }) {
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
