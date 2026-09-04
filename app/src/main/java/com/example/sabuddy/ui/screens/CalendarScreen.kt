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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.SwapHoriz
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sabuddy.data.CalendarEntryEntity
import com.example.sabuddy.ui.components.PlatformBadge
import com.example.sabuddy.ui.components.SaCard
import com.example.sabuddy.ui.components.StatusBadge
import com.example.sabuddy.ui.theme.LocalSaBuddyColors
import com.example.sabuddy.viewmodels.SaBuddyViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun CalendarScreen(
    viewModel: SaBuddyViewModel,
    modifier: Modifier = Modifier
) {
    val customColors = LocalSaBuddyColors.current
    val entries by viewModel.calendarEntries.collectAsStateWithLifecycle()
    val isAiLoading by viewModel.isAiLoading.collectAsStateWithLifecycle()
    val aiStatusMessage by viewModel.aiStatusMessage.collectAsStateWithLifecycle()

    var viewModeIndex by remember { mutableStateOf(0) } // 0: Weekly, 1: Monthly
    var showAddDialog by remember { mutableStateOf(false) }
    var editingEntry by remember { mutableStateOf<CalendarEntryEntity?>(null) }
    var movingEntry by remember { mutableStateOf<CalendarEntryEntity?>(null) }
    var showAiPlanDialog by remember { mutableStateOf(false) }

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val today = remember { dateFormat.format(Date()) }

    // Filter weekly or monthly
    val displayedEntries = remember(entries, viewModeIndex) {
        if (viewModeIndex == 0) {
            // Weekly: next 7 days
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, 7)
            val weekAhead = dateFormat.format(cal.time)
            entries.filter { it.date in today..weekAhead || it.date == today }
        } else {
            // Monthly: next 30 days
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, 30)
            val monthAhead = dateFormat.format(cal.time)
            entries.filter { it.date in today..monthAhead || it.date == today }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Content Calendar",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${displayedEntries.size} scheduled posts",
                    style = MaterialTheme.typography.bodySmall,
                    color = customColors.textMuted
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                    onClick = { showAiPlanDialog = true },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("AI Plan", fontSize = 12.sp)
                }

                IconButton(
                    onClick = { showAddDialog = true },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Post")
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Week vs Month View Toggle
        TabRow(
            selectedTabIndex = viewModeIndex,
            containerColor = customColors.cardBackground,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Tab(
                selected = viewModeIndex == 0,
                onClick = { viewModeIndex = 0 },
                text = { Text("Weekly View (7 Days)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
            )
            Tab(
                selected = viewModeIndex == 1,
                onClick = { viewModeIndex = 1 },
                text = { Text("Monthly View (30 Days)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold) }
            )
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
                    text = aiStatusMessage ?: "SaBuddy is planning and scheduling content...",
                    style = MaterialTheme.typography.bodySmall,
                    color = customColors.textMuted
                )
            }
        }

        // Calendar items
        if (displayedEntries.isEmpty() && !isAiLoading) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = customColors.textMuted, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No scheduled posts for this period.", color = customColors.textMuted)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(onClick = { showAiPlanDialog = true }) {
                        Text("Ask SaBuddy to plan 5 Reels")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(displayedEntries, key = { it.id }) { entry ->
                    CalendarEntryCard(
                        entry = entry,
                        isToday = entry.date == today,
                        onEdit = { editingEntry = entry },
                        onMove = { movingEntry = entry },
                        onDelete = { viewModel.deleteCalendarEntry(entry.id) },
                        onStatusChange = { newStatus ->
                            viewModel.updateCalendarEntry(entry.copy(status = newStatus))
                        }
                    )
                }
                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }

    // AI Plan Dialog ("Plan 5 Reels for next week")
    if (showAiPlanDialog) {
        var countInput by remember { mutableStateOf("5") }
        var topicInput by remember { mutableStateOf("Creator productivity and AI tools") }

        AlertDialog(
            onDismissRequest = { showAiPlanDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Auto-Plan with SaBuddy")
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "SaBuddy will generate unique content topics, platforms, and populate your calendar directly.",
                        style = MaterialTheme.typography.bodySmall,
                        color = customColors.textMuted
                    )
                    OutlinedTextField(
                        value = countInput,
                        onValueChange = { countInput = it },
                        label = { Text("Number of Posts (e.g. 5)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = topicInput,
                        onValueChange = { topicInput = it },
                        label = { Text("Main Focus or Niche") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val count = countInput.toIntOrNull() ?: 5
                        viewModel.generateCalendarPlan(days = count, topic = topicInput)
                        showAiPlanDialog = false
                    }
                ) {
                    Text("Populate Calendar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAiPlanDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Add Manual Entry Dialog
    if (showAddDialog) {
        var title by remember { mutableStateOf("") }
        var date by remember { mutableStateOf(today) }
        var platform by remember { mutableStateOf("Instagram") }
        var contentType by remember { mutableStateOf("Reel") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Scheduled Content") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = date,
                        onValueChange = { date = it },
                        label = { Text("Date (YYYY-MM-DD)") },
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
                        label = { Text("Content Type") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.saveCalendarEntry(
                            CalendarEntryEntity(
                                title = title.ifBlank { "Untitled Post" },
                                date = date,
                                platform = platform,
                                contentType = contentType,
                                status = "Ready"
                            )
                        )
                        showAddDialog = false
                    }
                ) {
                    Text("Schedule")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Move Date Dialog
    if (movingEntry != null) {
        val entry = movingEntry!!
        var newDate by remember { mutableStateOf(entry.date) }

        AlertDialog(
            onDismissRequest = { movingEntry = null },
            title = { Text("Move Content Date") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Moving: ${entry.title}")
                    OutlinedTextField(
                        value = newDate,
                        onValueChange = { newDate = it },
                        label = { Text("New Date (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateCalendarEntry(entry.copy(date = newDate))
                        movingEntry = null
                    }
                ) {
                    Text("Move")
                }
            },
            dismissButton = {
                TextButton(onClick = { movingEntry = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Edit Entry Dialog
    if (editingEntry != null) {
        val entry = editingEntry!!
        var title by remember { mutableStateOf(entry.title) }
        var platform by remember { mutableStateOf(entry.platform) }
        var contentType by remember { mutableStateOf(entry.contentType) }

        AlertDialog(
            onDismissRequest = { editingEntry = null },
            title = { Text("Edit Entry") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title") },
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
                        label = { Text("Content Type") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateCalendarEntry(
                            entry.copy(title = title, platform = platform, contentType = contentType)
                        )
                        editingEntry = null
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingEntry = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun CalendarEntryCard(
    entry: CalendarEntryEntity,
    isToday: Boolean,
    onEdit: () -> Unit,
    onMove: () -> Unit,
    onDelete: () -> Unit,
    onStatusChange: (String) -> Unit
) {
    val customColors = LocalSaBuddyColors.current
    var showStatusDropdown by remember { mutableStateOf(false) }

    SaCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (isToday) MaterialTheme.colorScheme.primary else customColors.tagBackground,
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, customColors.cardBorder)
                    ) {
                        Text(
                            text = if (isToday) "TODAY (${entry.date})" else entry.date,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isToday) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontSize = 10.sp
                        )
                    }
                    PlatformBadge(platform = entry.platform)
                    Text(
                        text = "• ${entry.contentType}",
                        style = MaterialTheme.typography.bodySmall,
                        color = customColors.textMuted
                    )
                }

                Box {
                    Surface(modifier = Modifier.clickable { showStatusDropdown = true }) {
                        StatusBadge(status = entry.status)
                    }

                    DropdownMenu(
                        expanded = showStatusDropdown,
                        onDismissRequest = { showStatusDropdown = false }
                    ) {
                        listOf("Idea", "Planning", "Ready", "Published").forEach { st ->
                            DropdownMenuItem(
                                text = { Text(st) },
                                onClick = {
                                    showStatusDropdown = false
                                    onStatusChange(st)
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = entry.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onMove, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.SwapHoriz, contentDescription = "Move Date", tint = customColors.textMuted, modifier = Modifier.size(16.dp))
                }
                IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = customColors.textMuted, modifier = Modifier.size(16.dp))
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
