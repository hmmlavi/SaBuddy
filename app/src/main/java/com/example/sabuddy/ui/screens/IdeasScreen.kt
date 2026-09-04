package com.example.sabuddy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
fun IdeasScreen(
    viewModel: SaBuddyViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val customColors = LocalSaBuddyColors.current
    val allContent by viewModel.allContentItems.collectAsStateWithLifecycle()
    val isAiLoading by viewModel.isAiLoading.collectAsStateWithLifecycle()
    val aiStatusMessage by viewModel.aiStatusMessage.collectAsStateWithLifecycle()

    var selectedFilter by remember { mutableStateOf("All") }
    var showGenerateDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<ContentItemEntity?>(null) }

    val ideasList = remember(allContent, selectedFilter) {
        val base = allContent.filter { it.category == "IDEAS" }
        if (selectedFilter == "All") base
        else base.filter { it.status.equals(selectedFilter, ignoreCase = true) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Top Action Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Content Ideas",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${ideasList.size} ideas brainstormed",
                    style = MaterialTheme.typography.bodySmall,
                    color = customColors.textMuted
                )
            }

            Button(
                onClick = { showGenerateDialog = true },
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Generate Idea", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Filter chips (All, Idea, Planning, Ready, Published)
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val filters = listOf("All", "Idea", "Planning", "Ready", "Published")
            items(filters) { filter ->
                val isSelected = selectedFilter == filter
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) MaterialTheme.colorScheme.primary else customColors.tagBackground,
                    border = androidx.compose.foundation.BorderStroke(
                        0.5.dp,
                        if (isSelected) MaterialTheme.colorScheme.primary else customColors.cardBorder
                    ),
                    modifier = Modifier.clickable { selectedFilter = filter }
                ) {
                    Text(
                        text = filter,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (isAiLoading) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = aiStatusMessage ?: "Generating ideas with SaBuddy...",
                    style = MaterialTheme.typography.bodySmall,
                    color = customColors.textMuted
                )
            }
        }

        // Ideas List
        if (ideasList.isEmpty() && !isAiLoading) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = customColors.textMuted,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No ideas found in this filter.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = customColors.textMuted
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(onClick = { showGenerateDialog = true }) {
                        Text("Brainstorm with SaBuddy")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(ideasList, key = { it.id }) { item ->
                    IdeaCardItem(
                        item = item,
                        onEdit = { editingItem = item },
                        onDelete = { viewModel.deleteContentItem(item) },
                        onAddToCalendar = { viewModel.addContentToCalendar(item) },
                        onCopy = {
                            val text = "${item.title}\n\nHook: ${item.hook}\n\n${item.body}"
                            copyToClipboard(context, text, "Idea copied to clipboard")
                        },
                        onStatusChange = { newStatus ->
                            viewModel.updateContentItem(item.copy(status = newStatus))
                        }
                    )
                }
                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }

    // Generate Idea Dialog
    if (showGenerateDialog) {
        var topic by remember { mutableStateOf("") }
        var platform by remember { mutableStateOf("Instagram") }
        var contentType by remember { mutableStateOf("Reel") }

        AlertDialog(
            onDismissRequest = { showGenerateDialog = false },
            title = { Text("Generate Idea with SaBuddy") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = topic,
                        onValueChange = { topic = it },
                        label = { Text("Topic or Theme") },
                        placeholder = { Text("e.g. 5 AI productivity tips") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = platform,
                        onValueChange = { platform = it },
                        label = { Text("Platform") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = contentType,
                        onValueChange = { contentType = it },
                        label = { Text("Content Type (Reel, Carousel, Post)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.generateIdea(topic, platform, contentType)
                        showGenerateDialog = false
                    }
                ) {
                    Text("Generate")
                }
            },
            dismissButton = {
                TextButton(onClick = { showGenerateDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Edit Item Dialog
    if (editingItem != null) {
        val item = editingItem!!
        var title by remember { mutableStateOf(item.title) }
        var hook by remember { mutableStateOf(item.hook) }
        var body by remember { mutableStateOf(item.body) }
        var status by remember { mutableStateOf(item.status) }

        AlertDialog(
            onDismissRequest = { editingItem = null },
            title = { Text("Edit Idea") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = hook,
                        onValueChange = { hook = it },
                        label = { Text("Hook") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = body,
                        onValueChange = { body = it },
                        label = { Text("Description / Outline") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateContentItem(
                            item.copy(title = title, hook = hook, body = body, status = status)
                        )
                        editingItem = null
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingItem = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun IdeaCardItem(
    item: ContentItemEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onAddToCalendar: () -> Unit,
    onCopy: () -> Unit,
    onStatusChange: (String) -> Unit
) {
    val customColors = LocalSaBuddyColors.current
    var showStatusMenu by remember { mutableStateOf(false) }

    SaCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PlatformBadge(platform = item.platform)
                    Text(
                        text = "• ${item.contentType}",
                        style = MaterialTheme.typography.bodySmall,
                        color = customColors.textMuted
                    )
                }

                Box {
                    Surface(
                        modifier = Modifier.clickable { showStatusMenu = true }
                    ) {
                        StatusBadge(status = item.status)
                    }

                    DropdownMenu(
                        expanded = showStatusMenu,
                        onDismissRequest = { showStatusMenu = false }
                    ) {
                        listOf("Idea", "Planning", "Ready", "Published").forEach { s ->
                            DropdownMenuItem(
                                text = { Text(s) },
                                onClick = {
                                    showStatusMenu = false
                                    onStatusChange(s)
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (item.hook.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "\"${item.hook}\"",
                    style = MaterialTheme.typography.bodySmall,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (item.body.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = item.body,
                    style = MaterialTheme.typography.bodySmall,
                    color = customColors.textMuted,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onCopy, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = customColors.textMuted, modifier = Modifier.size(15.dp))
                    }
                    IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = customColors.textMuted, modifier = Modifier.size(15.dp))
                    }
                    IconButton(onClick = onAddToCalendar, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = "Add to Calendar", tint = customColors.textMuted, modifier = Modifier.size(15.dp))
                    }
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444).copy(alpha = 0.8f), modifier = Modifier.size(15.dp))
                }
            }
        }
    }
}
