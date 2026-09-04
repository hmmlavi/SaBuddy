package com.example.sabuddy.viewmodels

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sabuddy.data.ActivityLogEntity
import com.example.sabuddy.data.AnalyticsEntity
import com.example.sabuddy.data.CalendarEntryEntity
import com.example.sabuddy.data.ChatMessageEntity
import com.example.sabuddy.data.ContentItemEntity
import com.example.sabuddy.data.PlatformProfileEntity
import com.example.sabuddy.data.PreferencesManager
import com.example.sabuddy.data.SaBuddyDatabase
import com.example.sabuddy.models.BrandKit
import com.example.sabuddy.models.CardConfig
import com.example.sabuddy.models.CardPreset
import com.example.sabuddy.models.FontSizePreference
import com.example.sabuddy.models.HashtagGroups
import com.example.sabuddy.models.NavigationStyle
import com.example.sabuddy.models.SaScreen
import com.example.sabuddy.models.SaTheme
import com.example.sabuddy.models.StructuredAiCard
import com.example.sabuddy.models.UiDensity
import com.example.sabuddy.services.GeminiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SaBuddyViewModel(application: Application) : AndroidViewModel(application) {

    private val db = SaBuddyDatabase.getDatabase(application)
    private val dao = db.saBuddyDao()
    private val prefs = PreferencesManager(application)
    private val geminiService = GeminiService()

    // Navigation & Screen state
    private val _currentScreen = MutableStateFlow(SaScreen.DASHBOARD)
    val currentScreen: StateFlow<SaScreen> = _currentScreen.asStateFlow()

    fun navigateTo(screen: SaScreen) {
        _currentScreen.value = screen
    }

    // Preferences & Customization
    val apiKey = prefs.apiKey
    val isSetupCompleted = prefs.isSetupCompleted
    val theme = prefs.theme
    val cardPreset = prefs.cardPreset
    val cornerRadius = prefs.cornerRadius
    val cardSpacing = prefs.cardSpacing
    val uiDensity = prefs.uiDensity
    val fontSize = prefs.fontSize
    val animationsEnabled = prefs.animationsEnabled
    val navStyle = prefs.navStyle
    val brandKit = prefs.brandKit
    val dashboardCards = prefs.dashboardCards

    // Database flows
    val chatMessages: StateFlow<List<ChatMessageEntity>> = dao.getAllChatMessages()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allContentItems: StateFlow<List<ContentItemEntity>> = dao.getAllContentItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val calendarEntries: StateFlow<List<CalendarEntryEntity>> = dao.getAllCalendarEntries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val platformProfiles: StateFlow<List<PlatformProfileEntity>> = dao.getAllPlatformProfiles()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentActivities: StateFlow<List<ActivityLogEntity>> = dao.getRecentActivityLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val analytics: StateFlow<AnalyticsEntity?> = dao.getAnalytics()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Search query state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    val filteredContentItems = combine(allContentItems, searchQuery) { items, query ->
        if (query.isBlank()) items
        else items.filter {
            it.title.contains(query, ignoreCase = true) ||
            it.caption.contains(query, ignoreCase = true) ||
            it.hook.contains(query, ignoreCase = true) ||
            it.platform.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // AI Operation state
    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    private val _aiStatusMessage = MutableStateFlow<String?>(null)
    val aiStatusMessage: StateFlow<String?> = _aiStatusMessage.asStateFlow()

    private val _lastGeneratedItem = MutableStateFlow<ContentItemEntity?>(null)
    val lastGeneratedItem: StateFlow<ContentItemEntity?> = _lastGeneratedItem.asStateFlow()

    private val _lastGeneratedHashtags = MutableStateFlow<HashtagGroups?>(null)
    val lastGeneratedHashtags: StateFlow<HashtagGroups?> = _lastGeneratedHashtags.asStateFlow()

    private val _lastGeneratedCaptions = MutableStateFlow<List<String>>(emptyList())
    val lastGeneratedCaptions: StateFlow<List<String>> = _lastGeneratedCaptions.asStateFlow()

    // Setup screen test state
    private val _isTestingKey = MutableStateFlow(false)
    val isTestingKey: StateFlow<Boolean> = _isTestingKey.asStateFlow()

    private val _keyTestResult = MutableStateFlow<String?>(null)
    val keyTestResult: StateFlow<String?> = _keyTestResult.asStateFlow()

    fun testApiKey(key: String, onComplete: ((Boolean) -> Unit)? = null) {
        viewModelScope.launch {
            _isTestingKey.value = true
            _keyTestResult.value = null
            val result = geminiService.testApiKey(key)
            _isTestingKey.value = false
            result.onSuccess {
                _keyTestResult.value = if (key.isBlank()) "Local Engine: Ready & active (No API key needed)" else "Success! Connected to Gemini."
                onComplete?.invoke(true)
            }.onFailure { err ->
                _keyTestResult.value = "Error: ${err.message ?: "Failed to connect"}"
                onComplete?.invoke(false)
            }
        }
    }

    fun saveApiKey(key: String) {
        prefs.saveApiKey(key)
    }

    fun removeApiKey() {
        prefs.removeApiKey()
    }

    // Appearance & Customization setters
    fun setTheme(theme: SaTheme) = prefs.setTheme(theme)
    fun setCardPreset(preset: CardPreset) = prefs.setCardPreset(preset)
    fun setCornerRadius(radius: Int) = prefs.setCornerRadius(radius)
    fun setCardSpacing(spacing: Int) = prefs.setCardSpacing(spacing)
    fun setUiDensity(density: UiDensity) = prefs.setUiDensity(density)
    fun setFontSize(size: FontSizePreference) = prefs.setFontSize(size)
    fun setAnimationsEnabled(enabled: Boolean) = prefs.setAnimationsEnabled(enabled)
    fun setNavStyle(style: NavigationStyle) = prefs.setNavStyle(style)
    fun saveBrandKit(kit: BrandKit) = prefs.saveBrandKit(kit)
    fun resetCardCustomization() = prefs.resetCardCustomization()
    fun resetDashboardLayout() = prefs.resetDashboardLayout()

    fun updateDashboardCards(cards: List<CardConfig>) {
        prefs.saveDashboardCards(cards)
    }

    fun toggleCardVisibility(cardId: String) {
        val current = dashboardCards.value
        val updated = current.map {
            if (it.id == cardId) it.copy(isVisible = !it.isVisible) else it
        }
        prefs.saveDashboardCards(updated)
    }

    fun setCardSize(cardId: String, newSize: String) {
        val current = dashboardCards.value
        val updated = current.map {
            if (it.id == cardId) it.copy(size = newSize) else it
        }
        prefs.saveDashboardCards(updated)
    }

    // --- AI Chat Actions ---
    fun sendChatMessage(
        text: String,
        attachedBitmap: Bitmap? = null,
        attachedImageUri: String? = null
    ) {
        if (text.isBlank() && attachedBitmap == null) return

        viewModelScope.launch {
            val userMsg = ChatMessageEntity(
                role = "user",
                content = text.ifBlank { "Analyze this screenshot / image" },
                imageUri = attachedImageUri
            )
            dao.insertChatMessage(userMsg)

            _isAiLoading.value = true
            _aiStatusMessage.value = "SaBuddy is thinking..."

            val history = chatMessages.value.takeLast(6).map { it.role to it.content }
            val result = geminiService.chatWithSaBuddy(
                apiKey = apiKey.value,
                prompt = text.ifBlank { "Analyze this screenshot in detail, identify strengths/weaknesses, and provide 3 actionable social media takeaways." },
                imageBitmap = attachedBitmap,
                brandKit = brandKit.value,
                conversationHistory = history
            )

            _isAiLoading.value = false
            _aiStatusMessage.value = null

            result.onSuccess { (responseText, structuredCard) ->
                val modelMsg = ChatMessageEntity(
                    role = "model",
                    content = responseText,
                    structuredType = structuredCard?.type ?: "NONE",
                    structuredTitle = structuredCard?.title,
                    structuredHook = structuredCard?.hook,
                    structuredScript = structuredCard?.script,
                    structuredCta = structuredCard?.cta,
                    structuredHashtags = structuredCard?.hashtags
                )
                dao.insertChatMessage(modelMsg)

                if (attachedBitmap != null) {
                    dao.insertActivityLog(
                        ActivityLogEntity(
                            action = "Analyzed Screenshot",
                            details = text.take(60).ifBlank { "Analyzed uploaded visual" }
                        )
                    )
                }
            }.onFailure { err ->
                val errorMsg = ChatMessageEntity(
                    role = "model",
                    content = "SaBuddy couldn't connect to Gemini: ${err.message}. Please check your internet connection or API key in Settings."
                )
                dao.insertChatMessage(errorMsg)
            }
        }
    }

    fun clearChat() {
        viewModelScope.launch {
            dao.clearChatMessages()
        }
    }

    // --- Content Generators ---
    fun generateIdea(topic: String, platform: String = "Instagram", contentType: String = "Reel") {
        viewModelScope.launch {
            _isAiLoading.value = true
            _aiStatusMessage.value = "Generating viral ideas with SaBuddy..."
            val result = geminiService.generateIdea(apiKey.value, topic, platform, contentType, brandKit.value)
            _isAiLoading.value = false
            _aiStatusMessage.value = null

            result.onSuccess { item ->
                _lastGeneratedItem.value = item
                saveContentItem(item)
                dao.insertActivityLog(ActivityLogEntity(action = "Generated Idea", details = item.title))
            }.onFailure { err ->
                _aiStatusMessage.value = "Failed: ${err.message}"
            }
        }
    }

    fun generateReel(topic: String) {
        viewModelScope.launch {
            _isAiLoading.value = true
            _aiStatusMessage.value = "Crafting high-converting Reel script..."
            val result = geminiService.generateReel(apiKey.value, topic, brandKit.value)
            _isAiLoading.value = false
            _aiStatusMessage.value = null

            result.onSuccess { reel ->
                _lastGeneratedItem.value = reel
                saveContentItem(reel)
                dao.insertActivityLog(ActivityLogEntity(action = "Created Reel", details = reel.title))
            }.onFailure { err ->
                _aiStatusMessage.value = "Failed: ${err.message}"
            }
        }
    }

    fun generatePost(topic: String, postType: String = "Carousel", platform: String = "LinkedIn") {
        viewModelScope.launch {
            _isAiLoading.value = true
            _aiStatusMessage.value = "Writing $postType for $platform..."
            val result = geminiService.generatePost(apiKey.value, topic, postType, platform, brandKit.value)
            _isAiLoading.value = false
            _aiStatusMessage.value = null

            result.onSuccess { post ->
                _lastGeneratedItem.value = post
                saveContentItem(post)
                dao.insertActivityLog(ActivityLogEntity(action = "Created Post", details = post.title))
            }.onFailure { err ->
                _aiStatusMessage.value = "Failed: ${err.message}"
            }
        }
    }

    fun generateCaptions(topic: String, platform: String, tone: String, length: String) {
        viewModelScope.launch {
            _isAiLoading.value = true
            _aiStatusMessage.value = "Generating captions..."
            val result = geminiService.generateCaptions(apiKey.value, topic, platform, tone, length, brandKit.value)
            _isAiLoading.value = false
            _aiStatusMessage.value = null

            result.onSuccess { captions ->
                _lastGeneratedCaptions.value = captions
                dao.insertActivityLog(ActivityLogEntity(action = "Generated Captions", details = topic.take(40)))
            }.onFailure { err ->
                _aiStatusMessage.value = "Failed: ${err.message}"
            }
        }
    }

    fun generateHashtags(topic: String, platform: String, count: Int = 15) {
        viewModelScope.launch {
            _isAiLoading.value = true
            _aiStatusMessage.value = "Finding top-performing hashtags..."
            val result = geminiService.generateHashtags(apiKey.value, topic, platform, count, brandKit.value)
            _isAiLoading.value = false
            _aiStatusMessage.value = null

            result.onSuccess { tags ->
                _lastGeneratedHashtags.value = tags
                dao.insertActivityLog(ActivityLogEntity(action = "Generated Hashtags", details = topic.take(40)))
            }.onFailure { err ->
                _aiStatusMessage.value = "Failed: ${err.message}"
            }
        }
    }

    fun generateCalendarPlan(days: Int = 5, topic: String = "Viral growth") {
        viewModelScope.launch {
            _isAiLoading.value = true
            _aiStatusMessage.value = "Planning content calendar with SaBuddy..."
            val result = geminiService.generateCalendarPlan(apiKey.value, days, topic, brandKit.value)
            _isAiLoading.value = false
            _aiStatusMessage.value = null

            result.onSuccess { entries ->
                entries.forEach { dao.insertCalendarEntry(it) }
                dao.insertActivityLog(ActivityLogEntity(action = "Updated Calendar", details = "Planned $days content pieces"))
            }.onFailure { err ->
                _aiStatusMessage.value = "Failed: ${err.message}"
            }
        }
    }

    // --- CRUD Operations ---
    fun saveContentItem(item: ContentItemEntity) {
        viewModelScope.launch {
            dao.insertContentItem(item)
            dao.insertActivityLog(ActivityLogEntity(action = "Saved ${item.category.lowercase()}", details = item.title))
        }
    }

    fun updateContentItem(item: ContentItemEntity) {
        viewModelScope.launch {
            dao.updateContentItem(item)
            dao.insertActivityLog(ActivityLogEntity(action = "Edited Content", details = item.title))
        }
    }

    fun deleteContentItem(item: ContentItemEntity) {
        viewModelScope.launch {
            dao.deleteContentItem(item)
        }
    }

    fun addContentToCalendar(item: ContentItemEntity, dateStr: String? = null) {
        viewModelScope.launch {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = dateStr ?: sdf.format(Date(System.currentTimeMillis() + 86400000L))
            dao.insertCalendarEntry(
                CalendarEntryEntity(
                    date = date,
                    platform = item.platform,
                    contentType = item.contentType,
                    title = item.title,
                    status = item.status,
                    contentItemId = item.id
                )
            )
            dao.insertActivityLog(ActivityLogEntity(action = "Updated Calendar", details = "${item.title} on $date"))
        }
    }

    fun saveCalendarEntry(entry: CalendarEntryEntity) {
        viewModelScope.launch {
            dao.insertCalendarEntry(entry)
            dao.insertActivityLog(ActivityLogEntity(action = "Updated Calendar", details = "${entry.title} (${entry.date})"))
        }
    }

    fun updateCalendarEntry(entry: CalendarEntryEntity) {
        viewModelScope.launch {
            dao.updateCalendarEntry(entry)
        }
    }

    fun deleteCalendarEntry(id: Long) {
        viewModelScope.launch {
            dao.deleteCalendarEntryById(id)
        }
    }

    fun updatePlatformProfile(profile: PlatformProfileEntity) {
        viewModelScope.launch {
            dao.insertPlatformProfile(profile)
            dao.insertActivityLog(ActivityLogEntity(action = "Updated Profile", details = "${profile.platform}: ${profile.username}"))
        }
    }

    fun updateAnalytics(followers: Long, views: Long, reach: Long, engagementRate: Float) {
        viewModelScope.launch {
            dao.saveAnalytics(
                AnalyticsEntity(
                    id = 1,
                    followers = followers,
                    views = views,
                    reach = reach,
                    engagementRate = engagementRate
                )
            )
            dao.insertActivityLog(ActivityLogEntity(action = "Updated Analytics", details = "$followers followers, $views views"))
        }
    }
}
