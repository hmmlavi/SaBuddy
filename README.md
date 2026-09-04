# ✦ SaBuddy — Your AI Social Media Copilot

SaBuddy is an all-in-one personal AI social media manager and content creation copilot built natively for Android. It helps creators research, draft, format, schedule, and audit multi-platform content from a single, beautiful workspace. 

By turning raw topics, prompts, or screenshots into ready-to-post scripts, multi-slide carousels, scroll-stopping hooks, and weekly editorial plans customized to your brand voice, SaBuddy acts as an indispensable creative partner.

---

## 🧭 System Workflow

SaBuddy runs on a single, continuous workflow loop to take ideas from raw inspiration to scheduled execution:

```text
Topic, Prompt, or Screenshot
            │
            ▼
   AI Chat or Creative Studio
            │
            ├─────────────────────────┐
            ▼ (Online Mode)           ▼ (Offline Fallback)
  [ Gemini 1.5 Flash ]       [ Local Creative Engine ]
            │                         │
            └────────────┬────────────┘
                         │
                         ▼
  Ideas · Reels · Posts · Captions · Hashtags
                         │
                         ▼
           Save to Local Content Library
                         │
                         ▼
            Schedule in Content Calendar
                         │
                         ▼
     Review Local Metrics & AI Profile Audit
```

---

## 🚀 Key Feature Studios

Each module within SaBuddy is fully inspected and verified to deliver highly optimized workflows:

### 🏠 1. Central Dashboard (`DashboardScreen.kt`)
* **Dynamic Pipeline Overview:** Shows an active SaBuddy AI greeting card, Today's Scheduled Content, Upcoming Content timeline, Quick Ideas drawer, Channel Analytics metric tiles, Connected Platforms, and Recent Activity logs.
* **Highly Customizable:** Any of the 9 layout cards can be dynamically toggled on or off via the Settings panel to suit your specific tracking needs.
* **Editable Metric Tiles:** Key metric values can be directly edited inside the app using a dedicated local dialog interface.

### 💬 2. SaBuddy AI Chat Studio (`ChatScreen.kt`)
* **Multimodal Visual Input:** Attach reference files or feed screenshots directly via Android’s zero-permission Photo Picker or capture new visual concepts using camera previews.
* **On-the-Fly UI Rendering:** Structural JSON blocks returned by the model are parsed in real time into interactive card templates inside the chat interface.
* **Instant Action Elements:** Save parsed outputs directly to the local library or write them directly into the editorial calendar.

### 💡 3. Content Ideas Generator (`IdeasScreen.kt`)
* **Strategic Ideation Frameworks:** Generates 5 structured ideas categorized under major content pillars: *Educational, Entertaining, Inspirational, Behind the Scenes,* and *Storytelling*.
* **Algorithm-Aware Filtering:** Filter and customize ideas by platform-specific priorities, matching your preferred content tone.
* **Persistent States:** Backed by full Room CRUD operations with dynamic bookmark states and direct calendar export options.

### 🎬 4. Reels Studio (`ReelsScreen.kt`)
* **Comprehensive Scripts:** Produces short-form video production guides containing visual hooks, first 3-second retention triggers, beat-by-beat breakdowns, voiceover lines, on-screen text, audio recommendations, and calls to action.
* **Pacing & Style Adapters:** Select video durations (*15s, 30s, 60s, 90s*) and choose between specific style profiles (*Talking Head, B-Roll & Voiceover, POV/Relatable, Step-by-Step Tutorial, Trend Adaptation*).

### 🖼️ 5. Posts & Carousels Studio (`PostsScreen.kt`)
* **Structured Carousels:** Generates slide-by-slide breakdowns including Cover Hook, Body Slides, and Closing Slide outlines for carousels ranging from *3 to 10 slides*.
* **Flexible Feed Formats:** Switch creation profiles to build *Single Image Captions, Long-Form Thought Leadership, or Poll / Audience Engagement prompts*.

### ✍️ 6. Captions & Hooks Studio (`CaptionsScreen.kt`)
* **Copywriting Frameworks:** Generates 5 distinct scroll-stopping hooks built on proven psychological approaches:
  - *Curiosity Gap* (withholds key details to drive reads)
  - *Contrarian* (challenges common industry expectations)
  - *Problem / Agitate* (highlights an audience pain point)
  - *Direct Benefit* (focuses on immediate value delivery)
  - *Story / Confession* (vulnerable narrative openings)
* **Caption Templates:** Produces 3 separate copywriting structures: *Short & Punchy, Storytelling/Personal,* and *Value/Educational* (complete with CTAs and targeted hashtags).

### #️⃣ 7. Hashtag Research Studio (`HashtagsScreen.kt`)
* **Tiered Hashtag Architecture:** Organizes tag recommendations into 4 strategic buckets:
  - *High Volume / Broad* (1M+ platform post volume)
  - *Medium Volume / Niche* (100k - 1M platform post volume)
  - *Low Volume / Micro-Community* (10k - 100k targeted post volume)
  - *Brand / Custom* (User-defined tags)
* **Presets & Quick Controls:** Fast platform-specific count configurations and single-tap copying options for full sets or individual tiers.

### 🗓️ 8. Content Calendar (`CalendarScreen.kt`)
* **Interactive Planning:** View upcoming pipelines in Weekly (7-day) or Monthly (30-day) grid interfaces.
* **Status Lifecycles:** Track and update publication states: `Draft`, `Scheduled`, or `Published`.
* **AI Content Planner:** Generate an AI 7-day plan that automatically builds a week of strategic content and writes those entries directly to the database.

### 📚 9. Saved Content Library (`SavedContentScreen.kt`)
* **Search Indexing:** Search across titles, content, hooks, and platform tags in real time.
* **Database Actions:** Full support for editing, deleting, copying, and rescheduling saved library items.

### 📊 10. Analytics & Profile Audit (`AnalyticsScreen.kt`)
* **Metrics Workspace:** Edit and audit core channel statistics (Followers, Views, Reach, Engagement, Demographics, Best Posting Times).
* **AI Profile Audits:** Evaluates locally stored statistics and niche configurations to generate custom assessments detailing strategic strengths, weaknesses, and a 30-day roadmap.

### 🎨 11. Brand Kit Studio (`BrandKitScreen.kt`)
* **Voice & Tone Consistency:** Stores your Brand Name, Niche/Industry, Target Audience, Brand Voice, Tone, Style, Goals, Do's, and Don'ts.
* **Automatic System Injection:** Persisted in `SharedPreferences` and systematically injected into all AI prompt structures and offline generation calls to ensure customized, on-brand output.

### ⚙️ 12. Settings & Theming Studio (`SettingsScreen.kt`)
* **Visual Customization:** Features 4 beautiful color palettes (*Pitch Black, Cyber Glow, Warm Editorial, Deep Ocean*), 3 card presets (*Modern Clean, Floating Card, Glassmorphism*), slider controls for corner radius (`0-28dp`) and card spacing (`4-24dp`), and navigation choices (*Modern Bottom Bar vs. Floating Nav*).

---

## 🤖 Dual-Engine Intelligence

SaBuddy uses a dual-engine architecture to ensure generation tools remain available whether you are online or offline.

### 1. Live Gemini Mode

When a Gemini API key is configured, SaBuddy communicates directly with the lightweight, powerful `gemini-1.5-flash` model.

* **REST Client:** Direct REST endpoint integration via `OkHttpClient`.
* **Timeout Settings:** Connect timeout of 30s and read/write timeouts of 60s for network stability.
* **Multimodal Visual Inputs:** Converts local images or screenshots to Base64-encoded JPEGs sent via inline payload blocks for design and layout critique.
* **Structured Responses:** Injects custom markdown rules instructing Gemini to return JSON blocks alongside natural language, enabling automated interactive card rendering inside the chat.

```text
Endpoint Configuration:
https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key={API_KEY}
```

---

### 2. Built-in Offline Fallback Mode

If no API key is provided, or if the device lacks internet access, `GeminiService` automatically activates its rule-based fallback system.

* **Zero External Dependencies:** Works in cell-dead zones, airplane mode, or during API outages.
* **Matching Generative Fallbacks:** Features custom localized templates for all creative studios:
  - `generateLocalIdeas()`
  - `generateLocalReels()`
  - `generateLocalPosts()`
  - `generateLocalCaptions()`
  - `generateLocalHashtags()`
  - `generateLocalChatResponse()`
  - `generateLocalCalendarPlan()`
  - `generateLocalAudit()`

> [!TIP]
> The app remains fully functional offline, using your local Brand Kit configurations to keep fallback ideas, scripts, and plans aligned with your brand identity.

---

## 🌐 Social Media Platform Support

SaBuddy supports content planning, platform formatting presets, visual badges, and metadata tracking for:

- **Instagram** (Reels scripts, carousel outlines, and grids)
- **TikTok** (Short-form scripting and hook formatting)
- **YouTube** (Shorts scripts, video descriptions, and formatting)
- **LinkedIn** (Long-form posts, carousels, and image updates)
- **X (Twitter)** (Short-form updates and poll configurations)
- **Facebook** (Feed posts, caption layouts, and scheduling)

> [!IMPORTANT]
> **API Sync Warning:**  
> This support uses local profile/handle configurations, organizational scheduling, and badges. There is **no live OAuth link**, account synchronization, or background automated publishing to social platforms. All metrics are updated manually on-device.

---

## 🧰 Technology Stack & Specifications

| Component | Technical Specification |
|---|---|
| **Programming Language** | Kotlin (`2.0.21`) |
| **UI Framework** | Jetpack Compose (100% declarative UI; zero legacy XML layouts; Android Edge-to-Edge enabled) |
| **Design System** | Material Design 3 (`M3`) with custom `SaBuddyTheme` |
| **Android SDK Support** | Minimum SDK: `26` (Android 8.0 Oreo) · Target SDK: `36` (Android 16) · Compile SDK: `36` |
| **Build System** | Gradle Kotlin DSL (`build.gradle.kts`) with central Version Catalog (`gradle/libs.versions.toml`) |
| **Android Gradle Plugin** | `8.9.0` |
| **Compose BOM** | `2025.02.00` |
| **Lifecycle Components** | `lifecycle-viewmodel-compose:2.8.7` · `activity-compose:1.10.1` |
| **Local Database** | Room Database (`2.6.1`) + `room-ktx` (KSP compiler) |
| **HTTP Engine** | OkHttp (`4.12.0`) |
| **JSON Serialization** | Kotlinx Serialization JSON (`1.7.3`) |
| **Image Loading** | Coil Compose (`2.7.0`) |
| **Asynchronous Engine** | Kotlinx Coroutines Android (`1.8.1`) |

---

## 🏗️ Architecture Specifications

SaBuddy is built on MVVM (Model-View-ViewModel) design principles and a Unidirectional Data Flow (UDF) pattern.

```text
Jetpack Compose Views (Screens & Layouts)
        │
        ▼ (UI Events / Interactions)
SaBuddyViewModel (State Holder)
        │
        ├───────────────────────────────┐
        ▼ (Database Actions)            ▼ (Settings / Theme Updates)
Room Database (SQLite)          PreferencesManager (SharedPreferences)
        │                               │
        ▼ (Reactive Flow Updates)       ▼ (Emits Updated Settings)
  State Flows ──────────────────────────┘
        │
        ▼ (collectAsStateWithLifecycle)
Declarative Compose UI Redraw
```

### Key Architectural Guidelines:
* **Single-Activity Architecture:** `MainActivity` acts as the single entry point, hosting `SaBuddyMainScaffold` to route all drawer, top bar, bottom navigation, and screen updates.
* **Unidirectional Flow:** Interaction events are passed directly to `SaBuddyViewModel`. The ViewModel mutates the local data layers and emits state changes back to lifecycle-aware Compose collectors.
* **Reactive Database Queries:** Database tables expose reactive Kotlin `Flow` queries, ensuring UI displays update immediately when database contents change.
* **Separated Service Interfaces:** `GeminiService` encapsulates network requests, Base64 processing, and offline fallback fallbacks, keeping ViewModels free of direct network code.

---

## 📁 Project Directory Structure

```text
app/src/main/java/com/example/
├── MainActivity.kt                          # Single Activity entry point
└── sabuddy/
    ├── data/
    │   ├── SaBuddyEntities.kt               # Room Database schemas (Content, Calendar, Platforms, Activity, Analytics)
    │   ├── SaBuddyDao.kt                    # Data Access Object with reactive Flow queries
    │   ├── SaBuddyDatabase.kt               # Local Room Database with seed callback
    │   └── PreferencesManager.kt            # Wrapper for local app settings, theme, brand kit, and API keys
    ├── models/
    │   └── SaModels.kt                      # Shared Enums, State Classes, and Structured Cards
    ├── services/
    │   └── GeminiService.kt                 # REST Network Client, Image Encoder, and Offline Fallback Engine
    ├── viewmodels/
    │   └── SaBuddyViewModel.kt              # App-wide State Machine, Database mutations, and AI orchestrator
    └── ui/
        ├── SaBuddyMainLayout.kt             # Scaffold navigation, Drawer, and screen routes
        ├── components/
        │   ├── CommonComponents.kt          # Shared UI elements (Custom Card wrapper, Platform Badges)
        │   └── AiStructuredCard.kt          # Specialized UI renderers for Structured JSON outputs
        ├── screens/
        │   ├── DashboardScreen.kt           # Central dashboard workspace
        │   ├── ChatScreen.kt                # Multimodal chat studio
        │   ├── IdeasScreen.kt               # Idea generator
        │   ├── ReelsScreen.kt               # Short-form script builder
        │   ├── PostsScreen.kt               # Text post & carousel designer
        │   ├── CaptionsScreen.kt            # Caption and hook editor
        │   ├── HashtagsScreen.kt            # Hashtag strategist
        │   ├── CalendarScreen.kt            # Calendar planning board
        │   ├── SavedContentScreen.kt        # Searchable saved repository
        │   ├── AnalyticsScreen.kt           # Local stats & growth audits
        │   ├── BrandKitScreen.kt            # Identity profile settings
        │   ├── SettingsScreen.kt            # Customization panel
        │   └── SetupScreen.kt               # Onboarding walk-through
        └── theme/
            ├── Color.kt                     # UI Theme color palettes
            ├── Theme.kt                     # SaBuddyTheme composable setup
            └── Type.kt                      # Material Design Typography
```

---

## ⚙️ Installation & Getting Started

### Development Requirements
To build and run SaBuddy locally, ensure your machine meets these requirements:

- **IDE:** Android Studio Ladybug (`2024.2.1`) or Meerkat (`2024.3.1`) or newer.
- **Java SDK:** JDK 17 (pre-configured with modern Gradle).
- **Android SDK:** Platforms for Android SDK 36 (compile & target) and SDK 26 (minimum execution floor).
- **Physical/Virtual Target:** Android Device/Emulator running Android 8.0 (Oreo / API 26) or newer.

### Step-by-Step Setup

1. **Clone the repository:**
   ```bash
   git clone https://github.com/hmmlavi/SaBuddy.git
   cd SaBuddy
   ```

2. **Open the project in Android Studio:**
   - Launch Android Studio, click **Open**, and navigate to the cloned `SaBuddy` directory.
   - Wait for the project structure to index and run initial Gradle sync tasks.

3. **Check SDK Configuration:**
   - Go to **Tools > SDK Manager** and confirm **Android 16 (Vanilla Ice Cream / API 36)** is installed.

4. **Running the App:**
   - Connect your Android device via USB debugging or start an emulator running API 26 or higher.
   - Select the `app` run configuration in the toolbar.
   - Click the **Run** button or press **Shift + F10** to compile and launch.

---

## 🔑 API Configuration & On-Device Security

SaBuddy is designed to be functional even without network access or cloud API keys.

* **Secure Preference Storage:** API keys are stored entirely on your local device inside private `SharedPreferences` (`sabuddy_prefs`).
* **Source Protection:** Keys are never hardcoded in source files or checked into version control.
* **Verification Utilities:** Verify active keys inside the application at any time using the built-in connectivity test powered by `GeminiService.testApiKey()`.

### Live Setup Steps:
1. Complete the onboarding screen when launching the app for the first time, or go to **Settings > Gemini API Key**.
2. Enter your key and test the connection.
3. Toggle off **Local Mode** to enable live AI responses across your tools.

---

## 📦 Command-Line Build Tools

You can compile, build, and package SaBuddy directly from the terminal:

* **Generate a Debug APK:**
  ```bash
  # macOS or Linux
  ./gradlew :app:assembleDebug

  # Windows PowerShell
  .\gradlew.bat :app:assembleDebug
  ```
  The compiled package will save to:  
  `app/build/outputs/apk/debug/app-debug.apk`

* **Clean Build Cache:**
  ```bash
  ./gradlew clean
  ```

* **Generate Unsigned Release APK:**
  ```bash
  ./gradlew :app:assembleRelease
  ```
  *Note: Release signing configurations must be added to build files before store distribution.*

---

## 💾 Local Storage Design

* **On-Device SQLite Engine:** Room database maintains five distinct structural tables (`Content`, `Calendar`, `Platform`, `Activity`, `Analytics`) to track your library items, scheduling logs, active handles, and manual performance stats.
* **Dynamic Flow Binding:** Database tables are bound directly to active views, updating the interface as actions occur.
* **On-Device SharedPreferences:** Handles app configurations, custom palette options, Brand Kit details, handles, and your API key.
* **Zero External Cloud Risk:** No cloud storage, remote database configurations, account-creation walls, or cross-device syncing are used. Your data stays entirely in your hands.

---

## ⚠️ Honest Limitations

SaBuddy is transparent about the boundaries of its current local-first version:

* **No Direct Social Publishing:** SaBuddy does not publish directly to social networks. It does not integrate social media OAuth login screens, store publish tokens, run automated background uploaders, or manage official publishing endpoints.
* **Manual Performance Metrics:** The Analytics Studio runs on-device. It does not pull statistics, view counts, follower data, or analytics graphs from third-party social media APIs.
* **No Background Notifications:** The Content Calendar does not schedule background alarms or device push notifications to alert you when scheduled posts are due.
* **Local-First Database:** Your content, calendar entries, settings, and keys are saved in local device storage. There is no automated cloud backup, server sync, or multi-user collaboration workflow.
* **Monolithic State ViewModel:** UI states, database actions, preferences, and API triggers flow through a single, central `SaBuddyViewModel` class.

---

## 🛣️ Future Roadmap

The limitations above represent possible features for future updates:

- [ ] Add direct platform share-sheet integrations to copy and launch official social apps.
- [ ] Add secure social platform OAuth links for official platform tools.
- [ ] Support automated performance analytics fetching.
- [ ] Add device-level alarms and push notifications for calendar reminders.
- [ ] Provide optional, end-to-end encrypted cloud backup configurations.
- [ ] Modularize the state architecture into per-feature ViewModels.

---

## 🤝 Contributing

Contributions to SaBuddy are welcome! If you want to suggest improvements or bug fixes:

1. **Fork the Repository** on GitHub.
2. **Create a Feature Branch** targeting your changes:
   ```bash
   git checkout -b feature/amazing-improvement
   ```
3. **Commit Your Updates:** Keep commits organized and descriptive.
4. **Push Your Changes:**
   ```bash
   git push origin feature/amazing-improvement
   ```
5. **Open a Pull Request:** Explain the scope and purpose of your modifications.

---

## 📄 License

This repository does not currently include a global open-source license. If you intend to use this project as a basis for your own application, please check back for updates or contact the repository owner [hmmlavi](https://github.com/hmmlavi) for usage guidelines.

---

## ⚖️ General Disclaimer

SaBuddy is an AI-assisted writing helper and editorial planner. All generated outputs (such as scripts, ideas, hashtags, captions, and strategies) should be reviewed and edited before publishing. Platform names are used strictly to describe content formatting, planning, and organizational workflows.

---

<div align="center">

### Built for creators who want a clearer, calmer content workflow.

✦ **SaBuddy — Plan with clarity. Create with confidence. Stay consistent.** ✦

**Built by [hmmlavi](https://github.com/hmmlavi)**

</div>
