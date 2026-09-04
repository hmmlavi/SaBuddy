package com.example.sabuddy.models

enum class SaScreen(val title: String) {
    DASHBOARD("Dashboard"),
    CHAT("AI Chat"),
    IDEAS("Ideas"),
    REELS("Reels"),
    POSTS("Posts"),
    CAPTIONS("Captions"),
    HASHTAGS("Hashtags"),
    CALENDAR("Calendar"),
    SAVED_CONTENT("Saved Content"),
    BRAND_KIT("Brand Kit"),
    ANALYTICS("Analytics"),
    SETTINGS("Settings")
}

enum class SaTheme(val displayName: String, val description: String) {
    ELEGANT_DARK("Elegant Dark", "Refined deep charcoal, ice blue, and slate accents"),
    MIDNIGHT("Midnight", "Deep charcoal, muted blue, soft gray"),
    OCEAN_MIST("Ocean Mist", "Soft blue, cool gray, muted teal"),
    SAGE("Sage", "Muted green, warm cream, soft gray"),
    LAVENDER("Lavender", "Soft purple, off-white, muted gray"),
    SAND("Sand", "Warm beige, soft brown, cream"),
    CLOUD("Cloud", "Soft white, blue-gray, light neutrals")
}

enum class CardPreset(val displayName: String) {
    COMPACT("Compact"),
    SOFT("Soft"),
    GLASS("Glass"),
    SOLID("Solid"),
    MINIMAL("Minimal")
}

enum class NavigationStyle(val displayName: String) {
    MODERN_SIDEBAR("Modern Sidebar"),
    COMPACT_SIDEBAR("Compact Sidebar"),
    FLOATING_NAV("Floating Navigation")
}

enum class UiDensity(val displayName: String) {
    COMPACT("Compact"),
    DEFAULT("Default"),
    COMFORTABLE("Comfortable")
}

enum class FontSizePreference(val displayName: String, val scale: Float) {
    SMALL("Small", 0.9f),
    DEFAULT("Default", 1.0f),
    LARGE("Large", 1.15f)
}

enum class ContentStatus(val displayName: String) {
    IDEA("Idea"),
    PLANNING("Planning"),
    READY("Ready"),
    PUBLISHED("Published")
}

enum class ContentCategory(val displayName: String) {
    IDEAS("Ideas"),
    REELS("Reels"),
    POSTS("Posts"),
    CAPTIONS("Captions"),
    HOOKS("Hooks"),
    HASHTAGS("Hashtags")
}

data class BrandKit(
    val brandName: String = "My Brand",
    val niche: String = "Tech & Productivity",
    val targetAudience: String = "Young professionals & creators",
    val brandVoice: String = "Friendly, insightful, encouraging",
    val tone: String = "Professional yet casual",
    val contentStyle: String = "Short-form video tips, carousels, crisp takeaways",
    val contentGoals: String = "Drive engagement, build community trust",
    val dos: String = "Use clear hooks, practical tips, high contrast visuals",
    val donts: String = "Avoid buzzwords, long rambling introductions, clickbait"
)

data class HashtagGroups(
    val highReach: List<String> = emptyList(),
    val niche: List<String> = emptyList(),
    val relevant: List<String> = emptyList()
)

data class StructuredAiCard(
    val type: String, // "REEL_IDEA", "POST", "CAPTION", "HASHTAGS"
    val title: String,
    val hook: String = "",
    val script: String = "",
    val cta: String = "",
    val hashtags: String = "",
    val body: String = ""
)

data class CardConfig(
    val id: String,
    val title: String,
    val isVisible: Boolean = true,
    val size: String = "MEDIUM" // "SMALL", "MEDIUM", "LARGE", "FULL"
)
