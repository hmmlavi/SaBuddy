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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.sabuddy.models.SaScreen
import com.example.sabuddy.ui.components.PlatformBadge
import com.example.sabuddy.ui.components.SaCard
import com.example.sabuddy.ui.theme.LocalSaBuddyColors
import com.example.sabuddy.viewmodels.SaBuddyViewModel
import java.util.Locale

@Composable
fun AnalyticsScreen(
    viewModel: SaBuddyViewModel,
    modifier: Modifier = Modifier
) {
    val customColors = LocalSaBuddyColors.current
    val analytics by viewModel.analytics.collectAsStateWithLifecycle()
    val platforms by viewModel.platformProfiles.collectAsStateWithLifecycle()

    var showEditDialog by remember { mutableStateOf(false) }

    val followers = analytics?.followers ?: 24850L
    val views = analytics?.views ?: 182400L
    val reach = analytics?.reach ?: 95200L
    val engagementRate = analytics?.engagementRate ?: 6.4f

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Analytics & Growth",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Track your creator metrics & get AI audits",
                    style = MaterialTheme.typography.bodySmall,
                    color = customColors.textMuted
                )
            }

            Button(
                onClick = { showEditDialog = true },
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Edit Stats", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // AI Audit Trigger Card
        SaCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "SaBuddy Performance Audit",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Ask SaBuddy to review your 30-day reach & suggest viral hooks to boost engagement.",
                        style = MaterialTheme.typography.bodySmall,
                        color = customColors.textMuted
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        viewModel.sendChatMessage("Analyze my current analytics: $followers followers, $views views, $reach reach, and $engagementRate% engagement rate. What are 3 concrete moves to double my reach next month?")
                        viewModel.navigateTo(SaScreen.CHAT)
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ask SaBuddy", fontSize = 11.5.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Key Metrics Grid
        SaCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "30-Day Performance Summary",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                MetricProgressBar(
                    label = "Total Followers",
                    value = formatNumber(followers),
                    growth = analytics?.followersGrowth ?: "+12.4%",
                    progress = 0.74f,
                    target = "Goal: 30K"
                )

                MetricProgressBar(
                    label = "Monthly Video Views",
                    value = formatNumber(views),
                    growth = analytics?.viewsGrowth ?: "+18.2%",
                    progress = 0.85f,
                    target = "Goal: 200K"
                )

                MetricProgressBar(
                    label = "Total Audience Reach",
                    value = formatNumber(reach),
                    growth = analytics?.reachGrowth ?: "+9.8%",
                    progress = 0.62f,
                    target = "Goal: 150K"
                )

                MetricProgressBar(
                    label = "Average Engagement Rate",
                    value = "$engagementRate%",
                    growth = analytics?.engagementGrowth ?: "+1.5%",
                    progress = (engagementRate / 10f).coerceIn(0f, 1f),
                    target = "Benchmark: 4.5%"
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Platform breakdown
        SaCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Connected Audience Channels",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                platforms.forEach { profile ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            PlatformBadge(platform = profile.platform)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (profile.username.isNotBlank()) profile.username else "Not connected",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (profile.username.isNotBlank()) MaterialTheme.colorScheme.onSurface else customColors.textMuted
                            )
                        }

                        Text(
                            text = if (profile.isConnected) "Active" else "Inactive",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (profile.isConnected) Color(0xFF10B981) else customColors.textMuted,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
    }

    if (showEditDialog) {
        var fInput by remember { mutableStateOf(followers.toString()) }
        var vInput by remember { mutableStateOf(views.toString()) }
        var rInput by remember { mutableStateOf(reach.toString()) }
        var eInput by remember { mutableStateOf(engagementRate.toString()) }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Update Analytics") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = fInput, onValueChange = { fInput = it }, label = { Text("Followers") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = vInput, onValueChange = { vInput = it }, label = { Text("30-Day Views") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = rInput, onValueChange = { rInput = it }, label = { Text("30-Day Reach") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = eInput, onValueChange = { eInput = it }, label = { Text("Engagement Rate (%)") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val f = fInput.toLongOrNull() ?: followers
                        val v = vInput.toLongOrNull() ?: views
                        val r = rInput.toLongOrNull() ?: reach
                        val e = eInput.toFloatOrNull() ?: engagementRate
                        viewModel.updateAnalytics(f, v, r, e)
                        showEditDialog = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun MetricProgressBar(
    label: String,
    value: String,
    growth: String,
    progress: Float,
    target: String
) {
    val customColors = LocalSaBuddyColors.current

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = customColors.textMuted)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = value, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = growth, style = MaterialTheme.typography.labelSmall, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(6.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = customColors.tagBackground,
        )
        Spacer(modifier = Modifier.height(2.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Text(text = target, style = MaterialTheme.typography.labelSmall, color = customColors.textMuted, fontSize = 10.sp)
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
