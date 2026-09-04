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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sabuddy.data.ContentItemEntity
import com.example.sabuddy.ui.components.PlatformBadge
import com.example.sabuddy.ui.components.SaCard
import com.example.sabuddy.ui.components.copyToClipboard
import com.example.sabuddy.ui.theme.LocalSaBuddyColors
import com.example.sabuddy.viewmodels.SaBuddyViewModel

@Composable
fun CaptionsScreen(
    viewModel: SaBuddyViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val customColors = LocalSaBuddyColors.current
    val isAiLoading by viewModel.isAiLoading.collectAsStateWithLifecycle()
    val captionsList by viewModel.lastGeneratedCaptions.collectAsStateWithLifecycle()

    var topic by remember { mutableStateOf("Why 90% of creators quit in their first 6 months") }
    var platform by remember { mutableStateOf("Instagram") }
    var tone by remember { mutableStateOf("Engaging & Empathetic") }
    var length by remember { mutableStateOf("Medium") }

    val platforms = listOf("Instagram", "LinkedIn", "TikTok", "YouTube", "X / Twitter")
    val tones = listOf("Casual & Friendly", "Authoritative", "Storytelling", "Humorous", "Direct")
    val lengths = listOf("Short & Punchy", "Medium", "Long Story")

    var editingCaptionIndex by remember { mutableStateOf<Int?>(null) }
    var editableText by remember { mutableStateOf("") }

    // Fallback sample captions if not generated yet
    val displayCaptions = if (captionsList.isNotEmpty()) {
        captionsList
    } else {
        listOf(
            "The biggest reason creators fail isn't the algorithm—it's burnout from doing everything alone. Here are 3 simple boundaries that saved my sanity this year. What's one habit keeping you sane right now? Drop it below 👇",
            "Stop waiting for perfection before you hit publish. Your first 30 posts are meant to be messy experiments. Learn fast, iterate daily, and watch the compound effect take over. Save this reminder for whenever self-doubt creeps in!",
            "3 harsh truths nobody tells you about building an audience in 2026:\n1. Followers ≠ revenue\n2. Retention beats virality every single time\n3. Consistency is a muscle, not an inspiration flash."
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Caption Generator",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Input Card
        SaCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = topic,
                    onValueChange = { topic = it },
                    label = { Text("Topic or Angle") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                // Selectors row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Platform selector
                    OutlinedTextField(
                        value = platform,
                        onValueChange = { platform = it },
                        label = { Text("Platform") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    // Tone selector
                    OutlinedTextField(
                        value = tone,
                        onValueChange = { tone = it },
                        label = { Text("Tone") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    // Length selector
                    OutlinedTextField(
                        value = length,
                        onValueChange = { length = it },
                        label = { Text("Length") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.generateCaptions(topic, platform, tone, length) },
                        enabled = !isAiLoading,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        if (isAiLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = MaterialTheme.colorScheme.onPrimary)
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Generate Captions", fontSize = 12.sp)
                        }
                    }

                    OutlinedButton(
                        onClick = { viewModel.generateCaptions(topic, platform, tone, length) },
                        enabled = !isAiLoading,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Regenerate", modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Regenerate", fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "Generated Caption Options",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(displayCaptions) { index, caption ->
                SaCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = "Option ${index + 1}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            PlatformBadge(platform = platform)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = caption,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Action Buttons: Copy, Save, Edit, Regenerate
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            OutlinedButton(
                                onClick = { copyToClipboard(context, caption, "Caption copied!") },
                                modifier = Modifier.weight(1f).height(32.dp),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Copy", fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    val item = ContentItemEntity(
                                        category = "CAPTIONS",
                                        title = "Caption for $topic".take(40),
                                        caption = caption,
                                        platform = platform,
                                        contentType = "Caption"
                                    )
                                    viewModel.saveContentItem(item)
                                    copyToClipboard(context, "", "Caption saved!")
                                },
                                modifier = Modifier.weight(1f).height(32.dp),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Icon(Icons.Outlined.BookmarkBorder, contentDescription = null, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Save", fontSize = 11.sp)
                            }

                            OutlinedButton(
                                onClick = {
                                    editingCaptionIndex = index
                                    editableText = caption
                                },
                                modifier = Modifier.weight(1f).height(32.dp),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Edit", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }

    // Edit Caption Dialog
    if (editingCaptionIndex != null) {
        AlertDialog(
            onDismissRequest = { editingCaptionIndex = null },
            title = { Text("Edit Caption") },
            text = {
                OutlinedTextField(
                    value = editableText,
                    onValueChange = { editableText = it },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val item = ContentItemEntity(
                            category = "CAPTIONS",
                            title = "Edited Caption".take(40),
                            caption = editableText,
                            platform = platform,
                            contentType = "Caption"
                        )
                        viewModel.saveContentItem(item)
                        editingCaptionIndex = null
                    }
                ) {
                    Text("Save to Library")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingCaptionIndex = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
