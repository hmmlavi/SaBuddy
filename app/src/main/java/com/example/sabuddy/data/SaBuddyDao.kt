package com.example.sabuddy.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SaBuddyDao {

    // --- Chat Messages ---
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    fun getAllChatMessages(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatMessage(message: ChatMessageEntity): Long

    @Query("DELETE FROM chat_messages")
    suspend fun clearChatMessages()

    // --- Content Items (Ideas, Reels, Posts, Captions, Hooks, Hashtags) ---
    @Query("SELECT * FROM content_items ORDER BY createdAt DESC")
    fun getAllContentItems(): Flow<List<ContentItemEntity>>

    @Query("SELECT * FROM content_items WHERE category = :category ORDER BY createdAt DESC")
    fun getContentItemsByCategory(category: String): Flow<List<ContentItemEntity>>

    @Query("SELECT * FROM content_items WHERE id = :id")
    suspend fun getContentItemById(id: Long): ContentItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContentItem(item: ContentItemEntity): Long

    @Update
    suspend fun updateContentItem(item: ContentItemEntity)

    @Delete
    suspend fun deleteContentItem(item: ContentItemEntity)

    @Query("DELETE FROM content_items WHERE id = :id")
    suspend fun deleteContentItemById(id: Long)

    @Query("SELECT * FROM content_items WHERE title LIKE '%' || :query || '%' OR caption LIKE '%' || :query || '%' OR hook LIKE '%' || :query || '%' ORDER BY createdAt DESC")
    fun searchContentItems(query: String): Flow<List<ContentItemEntity>>

    // --- Calendar Entries ---
    @Query("SELECT * FROM calendar_entries ORDER BY date ASC, timestamp ASC")
    fun getAllCalendarEntries(): Flow<List<CalendarEntryEntity>>

    @Query("SELECT * FROM calendar_entries WHERE date = :date")
    fun getCalendarEntriesByDate(date: String): Flow<List<CalendarEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCalendarEntry(entry: CalendarEntryEntity): Long

    @Update
    suspend fun updateCalendarEntry(entry: CalendarEntryEntity)

    @Query("DELETE FROM calendar_entries WHERE id = :id")
    suspend fun deleteCalendarEntryById(id: Long)

    // --- Platform Profiles ---
    @Query("SELECT * FROM platform_profiles")
    fun getAllPlatformProfiles(): Flow<List<PlatformProfileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlatformProfile(profile: PlatformProfileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPlatformProfiles(profiles: List<PlatformProfileEntity>)

    // --- Activity Logs ---
    @Query("SELECT * FROM activity_logs ORDER BY timestamp DESC LIMIT 20")
    fun getRecentActivityLogs(): Flow<List<ActivityLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivityLog(log: ActivityLogEntity)

    // --- Analytics ---
    @Query("SELECT * FROM analytics_records WHERE id = 1")
    fun getAnalytics(): Flow<AnalyticsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAnalytics(analytics: AnalyticsEntity)
}
