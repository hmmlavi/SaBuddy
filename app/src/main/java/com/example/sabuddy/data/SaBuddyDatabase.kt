package com.example.sabuddy.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Database(
    entities = [
        ChatMessageEntity::class,
        ContentItemEntity::class,
        CalendarEntryEntity::class,
        PlatformProfileEntity::class,
        ActivityLogEntity::class,
        AnalyticsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class SaBuddyDatabase : RoomDatabase() {

    abstract fun saBuddyDao(): SaBuddyDao

    companion object {
        @Volatile
        private var INSTANCE: SaBuddyDatabase? = null

        fun getDatabase(context: Context): SaBuddyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SaBuddyDatabase::class.java,
                    "sabuddy_database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database.saBuddyDao())
                    }
                }
            }

            private suspend fun populateInitialData(dao: SaBuddyDao) {
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val todayStr = dateFormat.format(Date())
                val tomorrowStr = dateFormat.format(Date(System.currentTimeMillis() + 86400000L))
                val inTwoDaysStr = dateFormat.format(Date(System.currentTimeMillis() + 86400000L * 2))
                val inThreeDaysStr = dateFormat.format(Date(System.currentTimeMillis() + 86400000L * 3))

                // Initial Chat Greeting
                dao.insertChatMessage(
                    ChatMessageEntity(
                        role = "model",
                        content = "Hey there! I'm SaBuddy, your personal AI social media manager. Ready to brainstorm killer hooks, plan high-converting Reels, or analyze your screenshots today?",
                        structuredType = "NONE"
                    )
                )

                // Today's Content (Instagram Reel "5 AI Tools You Should Know", status: Ready)
                dao.insertContentItem(
                    ContentItemEntity(
                        category = "REELS",
                        title = "5 AI Tools You Should Know",
                        hook = "Stop wasting 10 hours a week on manual editing. Here are 5 secret AI tools changing the game in 2026.",
                        script = "Hook: Stop wasting 10 hours a week on manual work.\n1. Tool 1: AI voiceover studio\n2. Tool 2: Caption animation automator\n3. Tool 3: Smart hook analyzer\nCTA: Comment 'TOOLS' to get the full list in your DMs!",
                        caption = "These 5 AI tools will easily 10x your creator workflow this month. Which one is your favorite? Drop a comment below! 👇",
                        cta = "Comment 'TOOLS' to get the full free PDF guide!",
                        hashtags = "#AITools #CreatorEconomy #SocialMediaTips #ReelsViral #ProductivityHacks #TechCreators",
                        platform = "Instagram",
                        contentType = "Reel",
                        status = "Ready",
                        scheduledDate = todayStr
                    )
                )

                // Upcoming Content Items
                dao.insertContentItem(
                    ContentItemEntity(
                        category = "POSTS",
                        title = "How to Hook Viewers in 3 Seconds",
                        hook = "Your first 3 seconds decide 90% of your video's retention.",
                        body = "1. Cut the fluff - never say 'Hey guys'\n2. Use visual motion in the first 0.5 seconds\n3. Introduce tension or curiosity before revealing the answer.",
                        caption = "Stop losing viewers on the opening frame. Here is the 3-second retention framework top creators swear by 📈",
                        cta = "Save this checklist for your next shoot!",
                        hashtags = "#ContentStrategy #HookFormula #SocialMediaGrowth #CreatorTips",
                        platform = "LinkedIn",
                        contentType = "Carousel",
                        status = "Planning",
                        scheduledDate = tomorrowStr
                    )
                )

                dao.insertContentItem(
                    ContentItemEntity(
                        category = "IDEAS",
                        title = "Day in the Life of a Solopreneur",
                        hook = "What running a 6-figure business from a coffee shop really looks like.",
                        body = "Authentic behind-the-scenes showing productivity habits, time blocks, and lessons learned.",
                        caption = "It's not all glamorous, but the freedom is unmatched. Here is an honest look at my daily schedule.",
                        cta = "What does your morning routine look like?",
                        hashtags = "#Solopreneur #MorningRoutine #BehindTheScenes #CreatorLife",
                        platform = "YouTube",
                        contentType = "Shorts",
                        status = "Idea",
                        scheduledDate = inTwoDaysStr
                    )
                )

                dao.insertContentItem(
                    ContentItemEntity(
                        category = "POSTS",
                        title = "3 Analytics Metrics That Actually Matter",
                        hook = "Vanity views won't pay the bills. Look at these instead.",
                        body = "Saves, Shares, and Watch Time Duration. If your audience saves your post, algorithm amplification triggers automatically.",
                        caption = "Stop obsessing over raw follower counts. Engagement depth and saves are what build true authority.",
                        cta = "Share this with a fellow creator who needs to hear this!",
                        hashtags = "#Analytics #SocialMetrics #GrowthHacks #CreatorInsights",
                        platform = "X / Twitter",
                        contentType = "Single Post",
                        status = "Ready",
                        scheduledDate = inThreeDaysStr
                    )
                )

                // Calendar entries
                dao.insertCalendarEntry(
                    CalendarEntryEntity(
                        date = todayStr,
                        platform = "Instagram",
                        contentType = "Reel",
                        title = "5 AI Tools You Should Know",
                        status = "Ready"
                    )
                )
                dao.insertCalendarEntry(
                    CalendarEntryEntity(
                        date = tomorrowStr,
                        platform = "LinkedIn",
                        contentType = "Carousel",
                        title = "How to Hook Viewers in 3 Seconds",
                        status = "Planning"
                    )
                )
                dao.insertCalendarEntry(
                    CalendarEntryEntity(
                        date = inTwoDaysStr,
                        platform = "YouTube",
                        contentType = "Shorts",
                        title = "Day in the Life of a Solopreneur",
                        status = "Idea"
                    )
                )
                dao.insertCalendarEntry(
                    CalendarEntryEntity(
                        date = inThreeDaysStr,
                        platform = "X / Twitter",
                        contentType = "Post",
                        title = "3 Analytics Metrics That Actually Matter",
                        status = "Ready"
                    )
                )

                // Platforms
                val platforms = listOf(
                    PlatformProfileEntity(platform = "Instagram", isConnected = true, username = "@yourbrand.ai", profileUrl = "https://instagram.com/yourbrand.ai"),
                    PlatformProfileEntity(platform = "YouTube", isConnected = true, username = "Creator Studio Hub", profileUrl = "https://youtube.com/@creatorstudio"),
                    PlatformProfileEntity(platform = "Facebook", isConnected = false, username = "", profileUrl = ""),
                    PlatformProfileEntity(platform = "LinkedIn", isConnected = true, username = "Alex Rivera", profileUrl = "https://linkedin.com/in/alexrivera"),
                    PlatformProfileEntity(platform = "X / Twitter", isConnected = true, username = "@alex_creatives", profileUrl = "https://x.com/alex_creatives"),
                    PlatformProfileEntity(platform = "TikTok", isConnected = false, username = "", profileUrl = "")
                )
                dao.insertAllPlatformProfiles(platforms)

                // Activity logs
                dao.insertActivityLog(ActivityLogEntity(action = "Created Reel", details = "5 AI Tools You Should Know for Instagram"))
                dao.insertActivityLog(ActivityLogEntity(action = "Generated Idea", details = "How to Hook Viewers in 3 Seconds"))
                dao.insertActivityLog(ActivityLogEntity(action = "Saved Caption", details = "Retention framework caption with 6 hashtags"))
                dao.insertActivityLog(ActivityLogEntity(action = "Updated Calendar", details = "Scheduled 4 posts across Instagram, LinkedIn, and YouTube"))
                dao.insertActivityLog(ActivityLogEntity(action = "Edited Content", details = "Refined CTA for solopreneur behind-the-scenes"))

                // Analytics
                dao.saveAnalytics(
                    AnalyticsEntity(
                        id = 1,
                        followers = 24850,
                        views = 182400,
                        reach = 95200,
                        engagementRate = 6.4f
                    )
                )
            }
        }
    }
}
