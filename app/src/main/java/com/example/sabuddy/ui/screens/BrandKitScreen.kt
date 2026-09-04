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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
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
import com.example.sabuddy.models.BrandKit
import com.example.sabuddy.ui.components.SaCard
import com.example.sabuddy.ui.components.copyToClipboard
import com.example.sabuddy.ui.theme.LocalSaBuddyColors
import com.example.sabuddy.viewmodels.SaBuddyViewModel

@Composable
fun BrandKitScreen(
    viewModel: SaBuddyViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val customColors = LocalSaBuddyColors.current
    val currentKit by viewModel.brandKit.collectAsStateWithLifecycle()

    var brandName by remember(currentKit) { mutableStateOf(currentKit.brandName) }
    var niche by remember(currentKit) { mutableStateOf(currentKit.niche) }
    var targetAudience by remember(currentKit) { mutableStateOf(currentKit.targetAudience) }
    var brandVoice by remember(currentKit) { mutableStateOf(currentKit.brandVoice) }
    var tone by remember(currentKit) { mutableStateOf(currentKit.tone) }
    var contentStyle by remember(currentKit) { mutableStateOf(currentKit.contentStyle) }
    var contentGoals by remember(currentKit) { mutableStateOf(currentKit.contentGoals) }
    var dos by remember(currentKit) { mutableStateOf(currentKit.dos) }
    var donts by remember(currentKit) { mutableStateOf(currentKit.donts) }

    var isSavedConfirmation by remember { mutableStateOf(false) }

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
                    text = "Brand Kit",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "SaBuddy uses this to personalize all AI content",
                    style = MaterialTheme.typography.bodySmall,
                    color = customColors.textMuted
                )
            }

            Button(
                onClick = {
                    val updated = BrandKit(
                        brandName = brandName,
                        niche = niche,
                        targetAudience = targetAudience,
                        brandVoice = brandVoice,
                        tone = tone,
                        contentStyle = contentStyle,
                        contentGoals = contentGoals,
                        dos = dos,
                        donts = donts
                    )
                    viewModel.saveBrandKit(updated)
                    isSavedConfirmation = true
                    copyToClipboard(context, "", "Brand Kit saved successfully!")
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Save Changes", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        SaCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Brand Identity",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                OutlinedTextField(
                    value = brandName,
                    onValueChange = { brandName = it },
                    label = { Text("Brand Name / Creator Handle") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = niche,
                    onValueChange = { niche = it },
                    label = { Text("Niche / Category") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = targetAudience,
                    onValueChange = { targetAudience = it },
                    label = { Text("Target Audience") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        SaCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Voice, Tone & Content Style",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                OutlinedTextField(
                    value = brandVoice,
                    onValueChange = { brandVoice = it },
                    label = { Text("Brand Voice") },
                    placeholder = { Text("e.g. Bold, encouraging, empathetic, witty") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = tone,
                    onValueChange = { tone = it },
                    label = { Text("Tone of Voice") },
                    placeholder = { Text("e.g. Conversational yet authoritative") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = contentStyle,
                    onValueChange = { contentStyle = it },
                    label = { Text("Content Style") },
                    placeholder = { Text("e.g. Short-form tips, story arcs, clear slides") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = contentGoals,
                    onValueChange = { contentGoals = it },
                    label = { Text("Primary Content Goals") },
                    placeholder = { Text("e.g. Drive newsletter signups, increase saves") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        SaCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Guardrails: Do's and Don'ts",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                OutlinedTextField(
                    value = dos,
                    onValueChange = { dos = it },
                    label = { Text("Do's") },
                    placeholder = { Text("e.g. Always include a 3-second hook, practical checklist") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                OutlinedTextField(
                    value = donts,
                    onValueChange = { donts = it },
                    label = { Text("Don'ts") },
                    placeholder = { Text("e.g. Never use corporate jargon, don't say 'Hey guys'") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedButton(
                onClick = {
                    val defaultKit = BrandKit()
                    brandName = defaultKit.brandName
                    niche = defaultKit.niche
                    targetAudience = defaultKit.targetAudience
                    brandVoice = defaultKit.brandVoice
                    tone = defaultKit.tone
                    contentStyle = defaultKit.contentStyle
                    contentGoals = defaultKit.contentGoals
                    dos = defaultKit.dos
                    donts = defaultKit.donts
                    viewModel.saveBrandKit(defaultKit)
                },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Reset to Default", fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(28.dp))
    }
}
