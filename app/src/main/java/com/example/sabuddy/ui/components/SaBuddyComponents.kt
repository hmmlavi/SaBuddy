package com.example.sabuddy.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sabuddy.models.StructuredAiCard
import com.example.sabuddy.ui.theme.LocalCornerRadius
import com.example.sabuddy.ui.theme.LocalSaBuddyColors

@Composable
fun SaCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    borderWidth: Dp = 1.dp,
    contentPadding: Dp = 14.dp,
    content: @Composable () -> Unit
) {
    val cornerRadius = LocalCornerRadius.current
    val customColors = LocalSaBuddyColors.current

    Card(
        modifier = if (onClick != null) modifier.clickable { onClick() } else modifier,
        shape = RoundedCornerShape(cornerRadius),
        colors = CardDefaults.cardColors(
            containerColor = customColors.cardBackground
        ),
        border = BorderStroke(borderWidth, customColors.cardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = customColors.cardElevation)
    ) {
        Box(modifier = Modifier.padding(contentPadding)) {
            content()
        }
    }
}

@Composable
fun SaSectionHeader(
    title: String,
    subtitle: String? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f, fill = false)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = LocalSaBuddyColors.current.textMuted
                )
            }
        }
        if (!actionText.isNullOrBlank() && onActionClick != null) {
            Text(
                text = actionText,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .clickable { onActionClick() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (bgColor, textColor) = when (status.lowercase()) {
        "ready" -> Pair(Color(0x264ADE80), Color(0xFF4ADE80))
        "planning" -> Pair(Color(0x26F59E0B), Color(0xFFFBBF24))
        "idea" -> Pair(Color(0x2660A5FA), Color(0xFF93C5FD))
        "published" -> Pair(Color(0x26A855F7), Color(0xFFC084FC))
        else -> Pair(Color(0x269CA3AF), Color(0xFFE2E2E6))
    }

    Surface(
        shape = RoundedCornerShape(100.dp),
        color = bgColor,
        border = BorderStroke(0.5.dp, textColor.copy(alpha = 0.35f))
    ) {
        Text(
            text = status.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.5.dp),
            fontSize = 9.5.sp
        )
    }
}

@Composable
fun PlatformBadge(platform: String) {
    val (badgeColor, dotColor) = when (platform.lowercase()) {
        "instagram" -> Pair(Color(0xFFE1306C), Color(0xFFF77737))
        "youtube" -> Pair(Color(0xFFFF0000), Color(0xFFFF5555))
        "linkedin" -> Pair(Color(0xFF0A66C2), Color(0xFF3B82F6))
        "tiktok" -> Pair(Color(0xFF00F2FE), Color(0xFF4FACFE))
        "facebook" -> Pair(Color(0xFF1877F2), Color(0xFF60A5FA))
        "x / twitter", "twitter", "x" -> Pair(Color(0xFF71717A), Color(0xFFE4E4E7))
        else -> Pair(Color(0xFF6366F1), Color(0xFF818CF8))
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(badgeColor.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
            .padding(horizontal = 7.dp, vertical = 2.5.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(dotColor, CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = platform,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = badgeColor,
            fontSize = 10.sp
        )
    }
}

@Composable
fun AiStructuredContentCard(
    card: StructuredAiCard,
    onSave: () -> Unit,
    onEdit: () -> Unit,
    onAddToCalendar: () -> Unit
) {
    val context = LocalContext.current
    val customColors = LocalSaBuddyColors.current

    SaCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        borderWidth = 1.2.dp
    ) {
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
                        text = card.type.replace("_", " "),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Row {
                    IconButton(
                        onClick = {
                            val textToCopy = buildString {
                                appendLine(card.title)
                                if (card.hook.isNotBlank()) appendLine("Hook: ${card.hook}")
                                if (card.script.isNotBlank()) appendLine("Script: ${card.script}")
                                if (card.body.isNotBlank()) appendLine(card.body)
                                if (card.cta.isNotBlank()) appendLine("CTA: ${card.cta}")
                                if (card.hashtags.isNotBlank()) appendLine(card.hashtags)
                            }
                            copyToClipboard(context, textToCopy, "Content copied to clipboard")
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy",
                            tint = customColors.textMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = card.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (card.hook.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "HOOK",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp
                )
                Text(
                    text = "\"${card.hook}\"",
                    style = MaterialTheme.typography.bodyMedium,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (card.script.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "SCRIPT",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp
                )
                Text(
                    text = card.script,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 6,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (card.cta.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "CTA",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFFF59E0B),
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp
                )
                Text(
                    text = "\"${card.cta}\"",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (card.hashtags.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = card.hashtags,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                OutlinedButton(
                    onClick = onSave,
                    modifier = Modifier.weight(1f).height(32.dp),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = ButtonDefaults.ContentPadding
                ) {
                    Icon(Icons.Outlined.BookmarkBorder, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Save", fontSize = 11.sp)
                }

                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f).height(32.dp),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = ButtonDefaults.ContentPadding
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit", fontSize = 11.sp)
                }

                OutlinedButton(
                    onClick = onAddToCalendar,
                    modifier = Modifier.weight(1.3f).height(32.dp),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = ButtonDefaults.ContentPadding
                ) {
                    Icon(Icons.Default.CalendarMonth, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("+ Calendar", fontSize = 11.sp)
                }
            }
        }
    }
}

fun copyToClipboard(context: Context, text: String, toastMsg: String = "Copied to clipboard") {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("SaBuddy Content", text)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT).show()
}
