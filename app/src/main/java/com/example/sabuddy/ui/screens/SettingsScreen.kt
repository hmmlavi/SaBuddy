package com.example.sabuddy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ViewQuilt
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.example.sabuddy.models.CardPreset
import com.example.sabuddy.models.NavigationStyle
import com.example.sabuddy.models.SaTheme
import com.example.sabuddy.models.UiDensity
import com.example.sabuddy.ui.components.PlatformBadge
import com.example.sabuddy.ui.components.SaCard
import com.example.sabuddy.ui.components.copyToClipboard
import com.example.sabuddy.ui.theme.LocalSaBuddyColors
import com.example.sabuddy.viewmodels.SaBuddyViewModel

@Composable
fun SettingsScreen(
    viewModel: SaBuddyViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val customColors = LocalSaBuddyColors.current

    val currentTheme by viewModel.theme.collectAsStateWithLifecycle()
    val currentPreset by viewModel.cardPreset.collectAsStateWithLifecycle()
    val cornerRadius by viewModel.cornerRadius.collectAsStateWithLifecycle()
    val cardSpacing by viewModel.cardSpacing.collectAsStateWithLifecycle()
    val uiDensity by viewModel.uiDensity.collectAsStateWithLifecycle()
    val animationsEnabled by viewModel.animationsEnabled.collectAsStateWithLifecycle()
    val navStyle by viewModel.navStyle.collectAsStateWithLifecycle()
    val dashboardCards by viewModel.dashboardCards.collectAsStateWithLifecycle()
    val currentApiKey by viewModel.apiKey.collectAsStateWithLifecycle()

    val isTestingKey by viewModel.isTestingKey.collectAsStateWithLifecycle()
    val keyTestResult by viewModel.keyTestResult.collectAsStateWithLifecycle()
    val platforms by viewModel.platformProfiles.collectAsStateWithLifecycle()

    var showChangeKeyDialog by remember { mutableStateOf(false) }
    var editingPlatformIndex by remember { mutableStateOf<Int?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Settings & Customization",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Fine-tune SaBuddy's AI, theme, cards, and platforms",
            style = MaterialTheme.typography.bodySmall,
            color = customColors.textMuted
        )

        Spacer(modifier = Modifier.height(14.dp))

        // SECTION 1: AI CREATIVE ENGINE
        SaCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Key, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AI Creative Engine",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    androidx.compose.foundation.layout.Box(
                        modifier = Modifier
                            .background(
                                color = Color(0xFF10B981).copy(alpha = 0.15f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (currentApiKey.isBlank()) "Local Mode" else "Custom Key",
                                color = Color(0xFF10B981),
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Text(
                    text = if (currentApiKey.isBlank()) {
                        "Your personal Gemini API key is NOT in use. SaBuddy runs with full built-in offline creative generation for viral ideas, reel scripts, carousels, captions, hashtags, and audits."
                    } else {
                        "Custom API key configured (${currentApiKey.take(4)}••••••••). Stored only locally on device."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = customColors.textMuted,
                    fontSize = 11.5.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { showChangeKeyDialog = true },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(if (currentApiKey.isBlank()) "Set Custom Key" else "Change Key", fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = { viewModel.testApiKey(currentApiKey) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Check Status", fontSize = 11.sp)
                    }

                    if (currentApiKey.isNotBlank()) {
                        OutlinedButton(
                            onClick = { viewModel.removeApiKey() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444))
                        ) {
                            Text("Remove", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // SECTION 2: APPEARANCE (THEMES, DENSITY, ANIMATIONS)
        SaCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Palette, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Appearance & Theme",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text("Theme Palette", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)

                val themes = SaTheme.values()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    themes.take(3).forEach { mode ->
                        ThemeOptionButton(
                            name = mode.displayName,
                            isSelected = currentTheme == mode,
                            onClick = { viewModel.setTheme(mode) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    themes.drop(3).forEach { mode ->
                        ThemeOptionButton(
                            name = mode.displayName,
                            isSelected = currentTheme == mode,
                            onClick = { viewModel.setTheme(mode) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text("UI Density", style = MaterialTheme.typography.labelSmall, color = customColors.textMuted)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    UiDensity.values().forEach { density ->
                        val isSelected = uiDensity == density
                        OutlinedButton(
                            onClick = { viewModel.setUiDensity(density) },
                            modifier = Modifier.weight(1f).height(32.dp),
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                            )
                        ) {
                            Text(density.displayName, fontSize = 11.sp)
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Smooth UI Animations", style = MaterialTheme.typography.bodySmall)
                    Switch(
                        checked = animationsEnabled,
                        onCheckedChange = { viewModel.setAnimationsEnabled(it) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // SECTION 3: CARD CUSTOMIZATION
        SaCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ViewQuilt, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Card Style & Geometry",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    CardPreset.values().forEach { preset ->
                        val isSelected = currentPreset == preset
                        OutlinedButton(
                            onClick = { viewModel.setCardPreset(preset) },
                            modifier = Modifier.weight(1f).height(30.dp),
                            shape = RoundedCornerShape(4.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                            )
                        ) {
                            Text(preset.displayName, fontSize = 9.5.sp, maxLines = 1)
                        }
                    }
                }

                Text("Corner Radius: $cornerRadius dp", style = MaterialTheme.typography.bodySmall)
                Slider(
                    value = cornerRadius.toFloat(),
                    onValueChange = { viewModel.setCornerRadius(it.toInt()) },
                    valueRange = 0f..28f
                )

                Text("Card Spacing: $cardSpacing dp", style = MaterialTheme.typography.bodySmall)
                Slider(
                    value = cardSpacing.toFloat(),
                    onValueChange = { viewModel.setCardSpacing(it.toInt()) },
                    valueRange = 4f..24f
                )

                OutlinedButton(
                    onClick = { viewModel.resetCardCustomization() },
                    modifier = Modifier.fillMaxWidth().height(32.dp),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reset Card Defaults", fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // SECTION 4: NAVIGATION STYLE
        SaCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Navigation Style",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    NavigationStyle.values().forEach { nav ->
                        val isSelected = navStyle == nav
                        OutlinedButton(
                            onClick = { viewModel.setNavStyle(nav) },
                            modifier = Modifier.weight(1f).height(32.dp),
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
                            )
                        ) {
                            Text(nav.displayName.split(" ")[0], fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // SECTION 5: DASHBOARD CARDS TOGGLE
        SaCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Dashboard Cards Visibility",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                dashboardCards.forEach { card ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = card.title, style = MaterialTheme.typography.bodySmall)
                        IconButton(
                            onClick = { viewModel.toggleCardVisibility(card.id) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                if (card.isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null,
                                tint = if (card.isVisible) MaterialTheme.colorScheme.primary else customColors.textMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // SECTION 6: CONNECTED PLATFORMS
        SaCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Connected Channels & Handles",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                platforms.forEachIndexed { index, profile ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            PlatformBadge(platform = profile.platform)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (profile.username.isNotBlank()) profile.username else "Tap to set handle",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (profile.username.isNotBlank()) MaterialTheme.colorScheme.onSurface else customColors.textMuted
                            )
                        }

                        Button(
                            onClick = { editingPlatformIndex = index },
                            modifier = Modifier.height(28.dp),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text("Edit", fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // SECTION 7: ABOUT SABUDDY
        SaCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "About SaBuddy",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Version: 1.0.0 (Production)\nModel: Google Gemini 2.5 Flash\nPrivacy: All calendar events, brand kit records, and generated drafts are stored exclusively on your local device via encrypted Room SQLite.",
                    style = MaterialTheme.typography.bodySmall,
                    color = customColors.textMuted,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }

    // Change Key Dialog
    if (showChangeKeyDialog) {
        var keyInput by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showChangeKeyDialog = false },
            title = { Text("Update Gemini API Key") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Enter your new Google Gemini API key:", fontSize = 12.sp)
                    OutlinedTextField(
                        value = keyInput,
                        onValueChange = { keyInput = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("AIzaSy...") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (keyInput.isNotBlank()) {
                            viewModel.saveApiKey(keyInput)
                            showChangeKeyDialog = false
                        }
                    }
                ) {
                    Text("Save Key")
                }
            },
            dismissButton = {
                TextButton(onClick = { showChangeKeyDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Edit Platform Dialog
    if (editingPlatformIndex != null) {
        val profile = platforms[editingPlatformIndex!!]
        var uname by remember { mutableStateOf(profile.username) }
        var isConn by remember { mutableStateOf(profile.isConnected) }

        AlertDialog(
            onDismissRequest = { editingPlatformIndex = null },
            title = { Text("Configure ${profile.platform}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = uname,
                        onValueChange = { uname = it },
                        label = { Text("Handle / Username") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Active Channel")
                        Switch(checked = isConn, onCheckedChange = { isConn = it })
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updatePlatformProfile(profile.copy(username = uname, isConnected = isConn))
                        editingPlatformIndex = null
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingPlatformIndex = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ThemeOptionButton(
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val customColors = LocalSaBuddyColors.current

    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(34.dp),
        shape = RoundedCornerShape(6.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
        ),
        border = androidx.compose.foundation.BorderStroke(
            if (isSelected) 1.5.dp else 0.5.dp,
            if (isSelected) MaterialTheme.colorScheme.primary else customColors.cardBorder
        )
    ) {
        Text(
            text = name,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            maxLines = 1
        )
    }
}
