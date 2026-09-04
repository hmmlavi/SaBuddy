package com.example.sabuddy.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val role: String, // "user" or "model"
    val content: String,
    val imageUri: String? = null,
    val structuredType: String = "NONE", // "REEL_IDEA", "POST", "CAPTION", "HASHTAGS", "NONE"
    val structuredTitle: String? = null,
    val structuredHook: String? = null,
    val structuredScript: String? = null,
    val structuredCta: String? = null,
    val structuredHashtags: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "content_items")
data class ContentItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String, // "IDEAS", "REELS", "POSTS", "CAPTIONS", "HOOKS", "HASHTAGS"
    val title: String,
    val hook: String = "",
    val body: String = "",
    val script: String = "",
    val caption: String = "",
    val cta: String = "",
    val hashtags: String = "",
    val platform: String = "Instagram", // Instagram, YouTube, Facebook, LinkedIn, X / Twitter, TikTok
    val contentType: String = "Reel", // Reel, Single Post, Carousel, Educational Post, Promotional Post
    val status: String = "Idea", // Idea, Planning, Ready, Published
    val scheduledDate: String? = null, // "YYYY-MM-DD"
    val isSaved: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "calendar_entries")
data class CalendarEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String, // "YYYY-MM-DD"
    val platform: String, // Instagram, YouTube, etc.
    val contentType: String, // Reel, Carousel, Post, etc.
    val title: String,
    val status: String = "Ready", // Idea, Planning, Ready, Published
    val contentItemId: Long? = null,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "platform_profiles")
data class PlatformProfileEntity(
    @PrimaryKey val platform: String, // "Instagram", "YouTube", "Facebook", "LinkedIn", "X / Twitter", "TikTok"
    val isConnected: Boolean = false,
    val username: String = "",
    val profileUrl: String = "",
    val notes: String = ""
)

@Entity(tableName = "activity_logs")
data class ActivityLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val action: String, // "Generated Idea", "Created Reel", "Saved Caption", "Updated Calendar", "Edited Content"
    val details: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "analytics_records")
data class AnalyticsEntity(
    @PrimaryKey val id: Int = 1,
    val followers: Long = 24850,
    val views: Long = 182400,
    val reach: Long = 95200,
    val engagementRate: Float = 6.4f,
    val followersGrowth: String = "+12.4%",
    val viewsGrowth: String = "+18.2%",
    val reachGrowth: String = "+9.8%",
    val engagementGrowth: String = "+1.5%",
    val lastUpdated: Long = System.currentTimeMillis()
)
