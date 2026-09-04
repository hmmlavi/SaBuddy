package com.example.sabuddy.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sabuddy.models.CardPreset
import com.example.sabuddy.models.FontSizePreference
import com.example.sabuddy.models.SaTheme
import com.example.sabuddy.models.UiDensity

data class SaBuddyCustomColors(
    val cardBackground: Color,
    val cardBorder: Color,
    val cardElevation: Dp,
    val accentBadge: Color,
    val textMuted: Color,
    val tagBackground: Color,
    val heroCardBackground: Color = Color(0xFF2D2F31),
    val heroCardBorder: Color = Color(0x6644474E),
    val iconContainerColor: Color = Color(0xFF3D4758),
    val successColor: Color = Color(0xFF4ADE80),
    val warningColor: Color = Color(0xFFF59E0B)
)

val LocalSaBuddyColors = compositionLocalOf {
    SaBuddyCustomColors(
        cardBackground = Color(0xFF1C1B1F),
        cardBorder = Color(0x6644474E),
        cardElevation = 0.dp,
        accentBadge = Color(0xFF4ADE80),
        textMuted = Color(0xFF919194),
        tagBackground = Color(0x66111318),
        heroCardBackground = Color(0xFF2D2F31),
        heroCardBorder = Color(0x6644474E),
        iconContainerColor = Color(0xFF3D4758),
        successColor = Color(0xFF4ADE80)
    )
}

val LocalCornerRadius = compositionLocalOf { 20.dp }
val LocalCardSpacing = compositionLocalOf { 12.dp }

// 0. ELEGANT DARK (Deep Charcoal #111318, Slate #2D2F31, Elevation #1C1B1F, Ice Blue #D1E4FF, Accent Navy #003062)
private val ElegantDarkColorScheme = darkColorScheme(
    primary = Color(0xFFD1E4FF), // Soft ice blue accent
    onPrimary = Color(0xFF003062), // Deep contrast navy on ice blue
    primaryContainer = Color(0xFF2D2F31), // Slate charcoal hero surface
    onPrimaryContainer = Color(0xFFD1E4FF),
    secondary = Color(0xFFAAC7FF), // Soft sky blue accent
    onSecondary = Color(0xFF003062),
    secondaryContainer = Color(0xFF3D4758), // Badge & icon surface
    onSecondaryContainer = Color(0xFFD1E4FF),
    tertiary = Color(0xFF4ADE80), // Vibrant green-400 accent
    onTertiary = Color(0xFF003914),
    tertiaryContainer = Color(0x264ADE80),
    onTertiaryContainer = Color(0xFF4ADE80),
    background = Color(0xFF111318), // Deep elegant dark background
    onBackground = Color(0xFFE2E2E6), // Light high-contrast text
    surface = Color(0xFF1C1B1F), // Sleek elevation surface
    onSurface = Color(0xFFE2E2E6),
    surfaceVariant = Color(0xFF2D2F31), // Secondary slate surface
    onSurfaceVariant = Color(0xFFC4C6D0),
    outline = Color(0xFF44474E), // Outline
    outlineVariant = Color(0x6644474E) // Outline with 40% alpha
)

// 1. MIDNIGHT (Deep charcoal, muted blue, soft gray)
private val MidnightColorScheme = darkColorScheme(
    primary = Color(0xFF38BDF8), // Muted sky blue
    onPrimary = Color(0xFF0F172A),
    primaryContainer = Color(0xFF1E3A5F),
    onPrimaryContainer = Color(0xFFBAE6FD),
    secondary = Color(0xFF2DD4BF), // Teal accent
    onSecondary = Color(0xFF0F172A),
    background = Color(0xFF0B0F19), // Deep charcoal
    onBackground = Color(0xFFF1F5F9),
    surface = Color(0xFF131B2E),
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF334155),
    outlineVariant = Color(0xFF1E293B)
)

// 2. OCEAN MIST (Soft blue, cool gray, muted teal)
private val OceanMistColorScheme = lightColorScheme(
    primary = Color(0xFF0284C7),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F2FE),
    onPrimaryContainer = Color(0xFF0369A1),
    secondary = Color(0xFF0D9488),
    onSecondary = Color.White,
    background = Color(0xFFF0F6FA), // Very soft ocean mist
    onBackground = Color(0xFF1E293B),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1),
    outlineVariant = Color(0xFFE2E8F0)
)

// 3. SAGE (Muted green, warm cream, soft gray)
private val SageColorScheme = lightColorScheme(
    primary = Color(0xFF437A62), // Muted sage green
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD8ECE1),
    onPrimaryContainer = Color(0xFF1B4D3E),
    secondary = Color(0xFF658872),
    onSecondary = Color.White,
    background = Color(0xFFF6F8F5), // Warm cream/sage wash
    onBackground = Color(0xFF232B25),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF232B25),
    surfaceVariant = Color(0xFFE3E9E4),
    onSurfaceVariant = Color(0xFF4F5E53),
    outline = Color(0xFFC7D3C9),
    outlineVariant = Color(0xFFDFE6E0)
)

// 4. LAVENDER (Soft purple, off-white, muted gray)
private val LavenderColorScheme = lightColorScheme(
    primary = Color(0xFF7C3AED), // Soft calm violet
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEDE9FE),
    onPrimaryContainer = Color(0xFF5B21B6),
    secondary = Color(0xFF9333EA),
    onSecondary = Color.White,
    background = Color(0xFFF9F8FD), // Off-white lavender tint
    onBackground = Color(0xFF1F1D2B),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1F1D2B),
    surfaceVariant = Color(0xFFEFEBF8),
    onSurfaceVariant = Color(0xFF5E5873),
    outline = Color(0xFFDCD5EE),
    outlineVariant = Color(0xFFEBE5F7)
)

// 5. SAND (Warm beige, soft brown, cream)
private val SandColorScheme = lightColorScheme(
    primary = Color(0xFF92603C), // Soft warm terracotta/brown
    onPrimary = Color.White,
    primaryContainer = Color(0xFFF3E8DF),
    onPrimaryContainer = Color(0xFF5F371B),
    secondary = Color(0xFFA27855),
    onSecondary = Color.White,
    background = Color(0xFFFAF7F2), // Warm beige / sand
    onBackground = Color(0xFF29231D),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF29231D),
    surfaceVariant = Color(0xFFEFE8DE),
    onSurfaceVariant = Color(0xFF6B5F54),
    outline = Color(0xFFDFD5C8),
    outlineVariant = Color(0xFFECE3D7)
)

// 6. CLOUD (Soft white, blue-gray, light neutral colors)
private val CloudColorScheme = lightColorScheme(
    primary = Color(0xFF334155), // Slate neutral
    onPrimary = Color.White,
    primaryContainer = Color(0xFFF1F5F9),
    onPrimaryContainer = Color(0xFF0F172A),
    secondary = Color(0xFF475569),
    onSecondary = Color.White,
    background = Color(0xFFF8FAFC), // Pure soft cloud
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF64748B),
    outline = Color(0xFFE2E8F0),
    outlineVariant = Color(0xFFF1F5F9)
)

fun getSaTypography(scale: Float): Typography {
    return Typography(
        headlineLarge = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = (28 * scale).sp,
            lineHeight = (34 * scale).sp,
            letterSpacing = (-0.5).sp
        ),
        headlineMedium = TextStyle(
            fontWeight = FontWeight.SemiBold,
            fontSize = (22 * scale).sp,
            lineHeight = (28 * scale).sp,
            letterSpacing = (-0.3).sp
        ),
        headlineSmall = TextStyle(
            fontWeight = FontWeight.SemiBold,
            fontSize = (18 * scale).sp,
            lineHeight = (24 * scale).sp
        ),
        titleLarge = TextStyle(
            fontWeight = FontWeight.SemiBold,
            fontSize = (17 * scale).sp,
            lineHeight = (22 * scale).sp
        ),
        titleMedium = TextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = (15 * scale).sp,
            lineHeight = (20 * scale).sp
        ),
        titleSmall = TextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = (13 * scale).sp,
            lineHeight = (18 * scale).sp
        ),
        bodyLarge = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = (15 * scale).sp,
            lineHeight = (22 * scale).sp
        ),
        bodyMedium = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = (13 * scale).sp,
            lineHeight = (19 * scale).sp
        ),
        bodySmall = TextStyle(
            fontWeight = FontWeight.Normal,
            fontSize = (11 * scale).sp,
            lineHeight = (16 * scale).sp
        ),
        labelLarge = TextStyle(
            fontWeight = FontWeight.SemiBold,
            fontSize = (13 * scale).sp,
            lineHeight = (18 * scale).sp
        ),
        labelMedium = TextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = (11 * scale).sp,
            lineHeight = (15 * scale).sp
        ),
        labelSmall = TextStyle(
            fontWeight = FontWeight.Medium,
            fontSize = (10 * scale).sp,
            lineHeight = (13 * scale).sp
        )
    )
}

@Composable
fun SaBuddyTheme(
    selectedTheme: SaTheme = SaTheme.ELEGANT_DARK,
    cardPreset: CardPreset = CardPreset.COMPACT,
    customCornerRadius: Int = 20,
    customCardSpacing: Int = 12,
    fontSizePreference: FontSizePreference = FontSizePreference.DEFAULT,
    content: @Composable () -> Unit
) {
    val colorScheme = when (selectedTheme) {
        SaTheme.ELEGANT_DARK -> ElegantDarkColorScheme
        SaTheme.MIDNIGHT -> MidnightColorScheme
        SaTheme.OCEAN_MIST -> OceanMistColorScheme
        SaTheme.SAGE -> SageColorScheme
        SaTheme.LAVENDER -> LavenderColorScheme
        SaTheme.SAND -> SandColorScheme
        SaTheme.CLOUD -> CloudColorScheme
    }

    val isDark = selectedTheme == SaTheme.ELEGANT_DARK || selectedTheme == SaTheme.MIDNIGHT
    val isElegantDark = selectedTheme == SaTheme.ELEGANT_DARK

    val customColors = when (cardPreset) {
        CardPreset.COMPACT -> SaBuddyCustomColors(
            cardBackground = if (isElegantDark) Color(0xFF1C1B1F) else if (isDark) Color(0xFF131B2E) else Color(0xFFFFFFFF),
            cardBorder = if (isElegantDark) Color(0x6644474E) else if (isDark) Color(0xFF1E293B) else Color(0xFFE2E8F0),
            cardElevation = if (isElegantDark) 0.dp else 1.dp,
            accentBadge = if (isElegantDark) Color(0xFF4ADE80) else colorScheme.primary,
            textMuted = if (isElegantDark) Color(0xFF919194) else if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
            tagBackground = if (isElegantDark) Color(0x66111318) else if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9),
            heroCardBackground = if (isElegantDark) Color(0xFF2D2F31) else if (isDark) Color(0xFF1E3A5F) else Color(0xFFE0F2FE),
            heroCardBorder = if (isElegantDark) Color(0x6644474E) else Color(0xFF334155),
            iconContainerColor = if (isElegantDark) Color(0xFF3D4758) else Color(0xFF1E293B),
            successColor = Color(0xFF4ADE80)
        )
        CardPreset.SOFT -> SaBuddyCustomColors(
            cardBackground = if (isElegantDark) Color(0xFF1C1B1F) else if (isDark) Color(0xFF172033) else Color(0xFFFFFFFF),
            cardBorder = if (isElegantDark) Color(0x4D44474E) else if (isDark) Color(0xFF26334D) else Color(0xFFEBF0F5),
            cardElevation = if (isElegantDark) 0.dp else 3.dp,
            accentBadge = if (isElegantDark) Color(0xFF4ADE80) else colorScheme.primary,
            textMuted = if (isElegantDark) Color(0xFF919194) else if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
            tagBackground = if (isElegantDark) Color(0x66111318) else if (isDark) Color(0xFF1E2A40) else Color(0xFFF4F7FB),
            heroCardBackground = if (isElegantDark) Color(0xFF2D2F31) else if (isDark) Color(0xFF1E3A5F) else Color(0xFFE0F2FE),
            heroCardBorder = if (isElegantDark) Color(0x6644474E) else Color(0xFF334155),
            iconContainerColor = if (isElegantDark) Color(0xFF3D4758) else Color(0xFF1E293B),
            successColor = Color(0xFF4ADE80)
        )
        CardPreset.GLASS -> SaBuddyCustomColors(
            cardBackground = if (isElegantDark) Color(0xCC1C1B1F) else if (isDark) Color(0xCC131B2E) else Color(0xEEFFFFFF),
            cardBorder = if (isElegantDark) Color(0x66D1E4FF) else if (isDark) Color(0x4438BDF8) else Color(0x660284C7),
            cardElevation = 0.dp,
            accentBadge = colorScheme.secondary,
            textMuted = if (isElegantDark) Color(0xFF919194) else if (isDark) Color(0xFFA0ABBA) else Color(0xFF556578),
            tagBackground = if (isElegantDark) Color(0x66111318) else if (isDark) Color(0x661E293B) else Color(0x44CBD5E1),
            heroCardBackground = if (isElegantDark) Color(0xCC2D2F31) else Color(0xFF1E3A5F),
            heroCardBorder = Color(0x6644474E),
            iconContainerColor = Color(0xFF3D4758),
            successColor = Color(0xFF4ADE80)
        )
        CardPreset.SOLID -> SaBuddyCustomColors(
            cardBackground = if (isElegantDark) Color(0xFF1C1B1F) else if (isDark) Color(0xFF1A233A) else Color(0xFFF9FAFB),
            cardBorder = if (isElegantDark) Color(0xFF44474E) else if (isDark) Color(0xFF334155) else Color(0xFFD1D5DB),
            cardElevation = 0.dp,
            accentBadge = colorScheme.primary,
            textMuted = if (isElegantDark) Color(0xFF919194) else if (isDark) Color(0xFF94A3B8) else Color(0xFF6B7280),
            tagBackground = if (isElegantDark) Color(0x66111318) else if (isDark) Color(0xFF0F172A) else Color(0xFFE5E7EB),
            heroCardBackground = Color(0xFF2D2F31),
            heroCardBorder = Color(0xFF44474E),
            iconContainerColor = Color(0xFF3D4758),
            successColor = Color(0xFF4ADE80)
        )
        CardPreset.MINIMAL -> SaBuddyCustomColors(
            cardBackground = if (isElegantDark) Color(0xFF16151A) else if (isDark) Color(0xFF0E1524) else Color(0xFFFFFFFF),
            cardBorder = if (isElegantDark) Color(0x3344474E) else if (isDark) Color(0xFF1A2438) else Color(0xFFF0F3F6),
            cardElevation = 0.dp,
            accentBadge = colorScheme.primary,
            textMuted = if (isElegantDark) Color(0xFF919194) else if (isDark) Color(0xFF8290A4) else Color(0xFF718096),
            tagBackground = if (isElegantDark) Color(0x44111318) else if (isDark) Color(0xFF141D2E) else Color(0xFFF7FAFC),
            heroCardBackground = Color(0xFF2D2F31),
            heroCardBorder = Color(0x3344474E),
            iconContainerColor = Color(0xFF3D4758),
            successColor = Color(0xFF4ADE80)
        )
    }

    val typography = getSaTypography(fontSizePreference.scale)

    CompositionLocalProvider(
        LocalSaBuddyColors provides customColors,
        LocalCornerRadius provides customCornerRadius.dp,
        LocalCardSpacing provides customCardSpacing.dp
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = typography,
            shapes = Shapes(
                small = RoundedCornerShape(customCornerRadius.dp / 2),
                medium = RoundedCornerShape(customCornerRadius.dp),
                large = RoundedCornerShape(customCornerRadius.dp + 4.dp)
            ),
            content = content
        )
    }
}
