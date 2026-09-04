package com.example.sabuddy.data

import android.content.Context
import android.content.SharedPreferences
import com.example.BuildConfig
import com.example.sabuddy.models.BrandKit
import com.example.sabuddy.models.CardConfig
import com.example.sabuddy.models.CardPreset
import com.example.sabuddy.models.FontSizePreference
import com.example.sabuddy.models.NavigationStyle
import com.example.sabuddy.models.SaTheme
import com.example.sabuddy.models.UiDensity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("sabuddy_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_API_KEY = "gemini_api_key"
        private const val KEY_SETUP_COMPLETED = "setup_completed"
        private const val KEY_THEME = "app_theme"
        private const val KEY_CARD_PRESET = "card_preset"
        private const val KEY_CORNER_RADIUS = "corner_radius"
        private const val KEY_CARD_SPACING = "card_spacing"
        private const val KEY_UI_DENSITY = "ui_density"
        private const val KEY_FONT_SIZE = "font_size"
        private const val KEY_ANIMATIONS = "animations_enabled"
        private const val KEY_NAV_STYLE = "nav_style"
        private const val KEY_DASHBOARD_CARDS = "dashboard_cards_json"

        // Brand Kit keys
        private const val KEY_BRAND_NAME = "brand_name"
        private const val KEY_BRAND_NICHE = "brand_niche"
        private const val KEY_BRAND_AUDIENCE = "brand_audience"
        private const val KEY_BRAND_VOICE = "brand_voice"
        private const val KEY_BRAND_TONE = "brand_tone"
        private const val KEY_BRAND_STYLE = "brand_style"
        private const val KEY_BRAND_GOALS = "brand_goals"
        private const val KEY_BRAND_DOS = "brand_dos"
        private const val KEY_BRAND_DONTS = "brand_donts"
    }

    private val _apiKey = MutableStateFlow(getSavedApiKey())
    val apiKey: StateFlow<String> = _apiKey.asStateFlow()

    private val _isSetupCompleted = MutableStateFlow(isSetupDone())
    val isSetupCompleted: StateFlow<Boolean> = _isSetupCompleted.asStateFlow()

    private val _theme = MutableStateFlow(getSavedTheme())
    val theme: StateFlow<SaTheme> = _theme.asStateFlow()

    private val _cardPreset = MutableStateFlow(getSavedCardPreset())
    val cardPreset: StateFlow<CardPreset> = _cardPreset.asStateFlow()

    private val _cornerRadius = MutableStateFlow(prefs.getInt(KEY_CORNER_RADIUS, 16))
    val cornerRadius: StateFlow<Int> = _cornerRadius.asStateFlow()

    private val _cardSpacing = MutableStateFlow(prefs.getInt(KEY_CARD_SPACING, 10))
    val cardSpacing: StateFlow<Int> = _cardSpacing.asStateFlow()

    private val _uiDensity = MutableStateFlow(getSavedDensity())
    val uiDensity: StateFlow<UiDensity> = _uiDensity.asStateFlow()

    private val _fontSize = MutableStateFlow(getSavedFontSize())
    val fontSize: StateFlow<FontSizePreference> = _fontSize.asStateFlow()

    private val _animationsEnabled = MutableStateFlow(prefs.getBoolean(KEY_ANIMATIONS, true))
    val animationsEnabled: StateFlow<Boolean> = _animationsEnabled.asStateFlow()

    private val _navStyle = MutableStateFlow(getSavedNavStyle())
    val navStyle: StateFlow<NavigationStyle> = _navStyle.asStateFlow()

    private val _brandKit = MutableStateFlow(getSavedBrandKit())
    val brandKit: StateFlow<BrandKit> = _brandKit.asStateFlow()

    private val _dashboardCards = MutableStateFlow(getSavedDashboardCards())
    val dashboardCards: StateFlow<List<CardConfig>> = _dashboardCards.asStateFlow()

    init {
        // Ensure no external or user API key is stored or required
        prefs.edit().remove(KEY_API_KEY).putBoolean(KEY_SETUP_COMPLETED, true).apply()
    }

    private fun getSavedApiKey(): String {
        // User requested: Don't use my Gemini API key.
        // App runs with full built-in offline creative capabilities.
        return ""
    }

    private fun isSetupDone(): Boolean {
        return true
    }

    fun saveApiKey(key: String) {
        // Kept for backward compatibility if needed, but not required
        prefs.edit().putBoolean(KEY_SETUP_COMPLETED, true).apply()
        _apiKey.value = key.trim()
        _isSetupCompleted.value = true
    }

    fun removeApiKey() {
        prefs.edit().remove(KEY_API_KEY).putBoolean(KEY_SETUP_COMPLETED, true).apply()
        _apiKey.value = ""
        _isSetupCompleted.value = true
    }

    private fun getSavedTheme(): SaTheme {
        val name = prefs.getString(KEY_THEME, SaTheme.ELEGANT_DARK.name)
        return runCatching { SaTheme.valueOf(name ?: SaTheme.ELEGANT_DARK.name) }.getOrDefault(SaTheme.ELEGANT_DARK)
    }

    fun setTheme(theme: SaTheme) {
        prefs.edit().putString(KEY_THEME, theme.name).apply()
        _theme.value = theme
    }

    private fun getSavedCardPreset(): CardPreset {
        val name = prefs.getString(KEY_CARD_PRESET, CardPreset.COMPACT.name)
        return runCatching { CardPreset.valueOf(name ?: CardPreset.COMPACT.name) }.getOrDefault(CardPreset.COMPACT)
    }

    fun setCardPreset(preset: CardPreset) {
        prefs.edit().putString(KEY_CARD_PRESET, preset.name).apply()
        _cardPreset.value = preset
        when (preset) {
            CardPreset.COMPACT -> { setCornerRadius(12); setCardSpacing(8) }
            CardPreset.SOFT -> { setCornerRadius(20); setCardSpacing(12) }
            CardPreset.GLASS -> { setCornerRadius(16); setCardSpacing(10) }
            CardPreset.SOLID -> { setCornerRadius(14); setCardSpacing(10) }
            CardPreset.MINIMAL -> { setCornerRadius(8); setCardSpacing(6) }
        }
    }

    fun setCornerRadius(radius: Int) {
        val clamped = radius.coerceIn(4, 32)
        prefs.edit().putInt(KEY_CORNER_RADIUS, clamped).apply()
        _cornerRadius.value = clamped
    }

    fun setCardSpacing(spacing: Int) {
        val clamped = spacing.coerceIn(4, 24)
        prefs.edit().putInt(KEY_CARD_SPACING, clamped).apply()
        _cardSpacing.value = clamped
    }

    private fun getSavedDensity(): UiDensity {
        val name = prefs.getString(KEY_UI_DENSITY, UiDensity.COMPACT.name)
        return runCatching { UiDensity.valueOf(name ?: UiDensity.COMPACT.name) }.getOrDefault(UiDensity.COMPACT)
    }

    fun setUiDensity(density: UiDensity) {
        prefs.edit().putString(KEY_UI_DENSITY, density.name).apply()
        _uiDensity.value = density
    }

    private fun getSavedFontSize(): FontSizePreference {
        val name = prefs.getString(KEY_FONT_SIZE, FontSizePreference.DEFAULT.name)
        return runCatching { FontSizePreference.valueOf(name ?: FontSizePreference.DEFAULT.name) }.getOrDefault(FontSizePreference.DEFAULT)
    }

    fun setFontSize(size: FontSizePreference) {
        prefs.edit().putString(KEY_FONT_SIZE, size.name).apply()
        _fontSize.value = size
    }

    fun setAnimationsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_ANIMATIONS, enabled).apply()
        _animationsEnabled.value = enabled
    }

    private fun getSavedNavStyle(): NavigationStyle {
        val name = prefs.getString(KEY_NAV_STYLE, NavigationStyle.MODERN_SIDEBAR.name)
        return runCatching { NavigationStyle.valueOf(name ?: NavigationStyle.MODERN_SIDEBAR.name) }.getOrDefault(NavigationStyle.MODERN_SIDEBAR)
    }

    fun setNavStyle(style: NavigationStyle) {
        prefs.edit().putString(KEY_NAV_STYLE, style.name).apply()
        _navStyle.value = style
    }

    private fun getSavedBrandKit(): BrandKit {
        return BrandKit(
            brandName = prefs.getString(KEY_BRAND_NAME, "My Brand") ?: "My Brand",
            niche = prefs.getString(KEY_BRAND_NICHE, "Tech & Productivity") ?: "Tech & Productivity",
            targetAudience = prefs.getString(KEY_BRAND_AUDIENCE, "Young professionals & creators") ?: "Young professionals & creators",
            brandVoice = prefs.getString(KEY_BRAND_VOICE, "Friendly, insightful, encouraging") ?: "Friendly, insightful, encouraging",
            tone = prefs.getString(KEY_BRAND_TONE, "Professional yet casual") ?: "Professional yet casual",
            contentStyle = prefs.getString(KEY_BRAND_STYLE, "Short-form video tips, carousels, crisp takeaways") ?: "Short-form video tips, carousels, crisp takeaways",
            contentGoals = prefs.getString(KEY_BRAND_GOALS, "Drive engagement, build community trust") ?: "Drive engagement, build community trust",
            dos = prefs.getString(KEY_BRAND_DOS, "Use clear hooks, practical tips, high contrast visuals") ?: "Use clear hooks, practical tips, high contrast visuals",
            donts = prefs.getString(KEY_BRAND_DONTS, "Avoid buzzwords, long rambling introductions, clickbait") ?: "Avoid buzzwords, long rambling introductions, clickbait"
        )
    }

    fun saveBrandKit(kit: BrandKit) {
        prefs.edit()
            .putString(KEY_BRAND_NAME, kit.brandName)
            .putString(KEY_BRAND_NICHE, kit.niche)
            .putString(KEY_BRAND_AUDIENCE, kit.targetAudience)
            .putString(KEY_BRAND_VOICE, kit.brandVoice)
            .putString(KEY_BRAND_TONE, kit.tone)
            .putString(KEY_BRAND_STYLE, kit.contentStyle)
            .putString(KEY_BRAND_GOALS, kit.contentGoals)
            .putString(KEY_BRAND_DOS, kit.dos)
            .putString(KEY_BRAND_DONTS, kit.donts)
            .apply()
        _brandKit.value = kit
    }

    private fun defaultCards(): List<CardConfig> {
        return listOf(
            CardConfig("sabuddy_ai", "SaBuddy AI", isVisible = true, size = "FULL"),
            CardConfig("today_content", "Today's Content", isVisible = true, size = "LARGE"),
            CardConfig("upcoming_content", "Upcoming Content", isVisible = true, size = "MEDIUM"),
            CardConfig("content_ideas", "Content Ideas", isVisible = true, size = "MEDIUM"),
            CardConfig("analytics", "Analytics", isVisible = true, size = "FULL"),
            CardConfig("quick_actions", "Quick Actions", isVisible = true, size = "FULL"),
            CardConfig("connected_platforms", "Connected Platforms", isVisible = true, size = "LARGE"),
            CardConfig("recent_activity", "Recent Activity", isVisible = true, size = "MEDIUM"),
            CardConfig("saved_content", "Saved Content", isVisible = true, size = "FULL")
        )
    }

    private fun getSavedDashboardCards(): List<CardConfig> {
        val raw = prefs.getString(KEY_DASHBOARD_CARDS, null) ?: return defaultCards()
        return try {
            val array = JSONArray(raw)
            val list = mutableListOf<CardConfig>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    CardConfig(
                        id = obj.getString("id"),
                        title = obj.getString("title"),
                        isVisible = obj.getBoolean("isVisible"),
                        size = obj.optString("size", "MEDIUM")
                    )
                )
            }
            if (list.isEmpty()) defaultCards() else list
        } catch (e: Exception) {
            defaultCards()
        }
    }

    fun saveDashboardCards(cards: List<CardConfig>) {
        val array = JSONArray()
        cards.forEach { card ->
            val obj = JSONObject()
            obj.put("id", card.id)
            obj.put("title", card.title)
            obj.put("isVisible", card.isVisible)
            obj.put("size", card.size)
            array.put(obj)
        }
        prefs.edit().putString(KEY_DASHBOARD_CARDS, array.toString()).apply()
        _dashboardCards.value = cards
    }

    fun resetCardCustomization() {
        setCardPreset(CardPreset.COMPACT)
        setCornerRadius(12)
        setCardSpacing(8)
    }

    fun resetDashboardLayout() {
        saveDashboardCards(defaultCards())
    }
}
