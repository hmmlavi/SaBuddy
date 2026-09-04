package com.example.sabuddy.services

import android.graphics.Bitmap
import android.util.Base64
import com.example.sabuddy.data.CalendarEntryEntity
import com.example.sabuddy.data.ContentItemEntity
import com.example.sabuddy.models.BrandKit
import com.example.sabuddy.models.HashtagGroups
import com.example.sabuddy.models.StructuredAiCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class GeminiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()
    private val modelName = "gemini-2.5-flash"
    private val baseUrl = "https://generativelanguage.googleapis.com/v1beta/models/$modelName:generateContent"

    suspend fun testApiKey(apiKey: String): Result<Boolean> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            // Built-in offline AI engine is always active
            return@withContext Result.success(true)
        }
        try {
            val url = "$baseUrl?key=${apiKey.trim()}"
            val requestJson = JSONObject().apply {
                val contents = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val parts = JSONArray().apply {
                            put(JSONObject().put("text", "Hello, reply with only 'OK'."))
                        }
                        put("parts", parts)
                    }
                    put(contentObj)
                }
                put("contents", contents)
            }

            val request = Request.Builder()
                .url(url)
                .post(requestJson.toString().toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                val errorMsg = try {
                    val errJson = JSONObject(responseBody)
                    errJson.optJSONObject("error")?.optString("message")
                        ?: "HTTP ${response.code}: $responseBody"
                } catch (e: Exception) {
                    "HTTP ${response.code}: $responseBody"
                }
                return@withContext Result.failure(Exception(errorMsg))
            }

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun chatWithSaBuddy(
        apiKey: String,
        prompt: String,
        imageBitmap: Bitmap?,
        brandKit: BrandKit?,
        conversationHistory: List<Pair<String, String>> = emptyList()
    ): Result<Pair<String, StructuredAiCard?>> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext Result.success(
                generateLocalChatResponse(prompt, imageBitmap, brandKit, conversationHistory)
            )
        }

        try {
            val url = "$baseUrl?key=${apiKey.trim()}"
            val systemInstruction = buildSystemPrompt(brandKit)

            val requestJson = JSONObject().apply {
                // System instruction
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", systemInstruction))
                    })
                })

                val contentsArray = JSONArray()

                // Recent conversation history (up to last 6 messages)
                val recentHistory = conversationHistory.takeLast(6)
                for (turn in recentHistory) {
                    val role = if (turn.first == "user") "user" else "model"
                    contentsArray.put(JSONObject().apply {
                        put("role", role)
                        put("parts", JSONArray().apply {
                            put(JSONObject().put("text", turn.second))
                        })
                    })
                }

                // Current message
                val currentParts = JSONArray()
                currentParts.put(JSONObject().put("text", prompt))

                if (imageBitmap != null) {
                    val base64Data = bitmapToBase64(imageBitmap)
                    currentParts.put(JSONObject().apply {
                        put("inlineData", JSONObject().apply {
                            put("mimeType", "image/jpeg")
                            put("data", base64Data)
                        })
                    })
                }

                contentsArray.put(JSONObject().apply {
                    put("role", "user")
                    put("parts", currentParts)
                })

                put("contents", contentsArray)

                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.7)
                    put("maxOutputTokens", 2048)
                })
            }

            val request = Request.Builder()
                .url(url)
                .post(requestJson.toString().toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                // Graceful fallback to local response instead of failing
                return@withContext Result.success(
                    generateLocalChatResponse(prompt, imageBitmap, brandKit, conversationHistory)
                )
            }

            val textResult = parseCandidateText(responseBody)
            val structuredCard = extractStructuredCard(textResult)

            Result.success(Pair(textResult, structuredCard))
        } catch (e: Exception) {
            // Graceful fallback on network or parsing error
            Result.success(generateLocalChatResponse(prompt, imageBitmap, brandKit, conversationHistory))
        }
    }

    suspend fun generateIdea(
        apiKey: String,
        topic: String,
        platform: String,
        contentType: String,
        brandKit: BrandKit?
    ): Result<ContentItemEntity> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext Result.success(generateLocalIdea(topic, platform, contentType, brandKit))
        }

        val prompt = """
            Generate an awesome social media content idea.
            Topic: ${if (topic.isBlank()) "Viral trends in our niche" else topic}
            Platform: $platform
            Content Type: $contentType

            Please format your response strictly as JSON with this structure:
            {
              "title": "Short catchy title",
              "hook": "Attention-grabbing opening hook",
              "body": "Key points / outline for the content",
              "caption": "Engaging caption with call to action",
              "cta": "Clear call to action",
              "hashtags": "#Tag1 #Tag2 #Tag3 #Tag4 #Tag5 #Tag6"
            }
            Do not include markdown fences, just the raw JSON or json block.
        """.trimIndent()

        val response = callGeminiRaw(apiKey, prompt, null, brandKit)
        if (response.isSuccess) {
            val rawText = response.getOrNull() ?: ""
            Result.success(parseContentItemJson(rawText, "IDEAS", platform, contentType))
        } else {
            Result.success(generateLocalIdea(topic, platform, contentType, brandKit))
        }
    }

    suspend fun generateReel(
        apiKey: String,
        topic: String,
        brandKit: BrandKit?
    ): Result<ContentItemEntity> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext Result.success(generateLocalReel(topic, brandKit))
        }

        val prompt = """
            Create a complete, high-converting short-form Reel / TikTok / Shorts script.
            Topic: ${if (topic.isBlank()) "High value tip in our niche" else topic}
            Platform: Instagram Reel

            Format strictly as JSON:
            {
              "title": "Catchy Reel Title",
              "hook": "First 3 seconds verbal + visual hook",
              "script": "Full second-by-second script breakdown (Hook 0-3s, Problem 3-10s, Value points 10-40s, Climax 40-50s, CTA 50-60s)",
              "caption": "Short punchy caption teasing the reel",
              "cta": "Comment 'REEL' or save for later",
              "hashtags": "#ReelsTips #ViralReels #CreatorTips #GrowthFormula"
            }
        """.trimIndent()

        val response = callGeminiRaw(apiKey, prompt, null, brandKit)
        if (response.isSuccess) {
            val rawText = response.getOrNull() ?: ""
            Result.success(parseContentItemJson(rawText, "REELS", "Instagram", "Reel"))
        } else {
            Result.success(generateLocalReel(topic, brandKit))
        }
    }

    suspend fun generatePost(
        apiKey: String,
        topic: String,
        postType: String,
        platform: String,
        brandKit: BrandKit?
    ): Result<ContentItemEntity> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext Result.success(generateLocalPost(topic, postType, platform, brandKit))
        }

        val prompt = """
            Write a complete $postType for $platform.
            Topic: ${if (topic.isBlank()) "Key lessons learned this week" else topic}
            Type: $postType

            Format strictly as JSON:
            {
              "title": "Post Title",
              "hook": "Headline hook or Slide 1 text",
              "body": "Full body text or Slide by slide carousel breakdown",
              "caption": "Accompanying caption",
              "cta": "Engaging closing CTA",
              "hashtags": "#RelevantTag1 #RelevantTag2 #RelevantTag3"
            }
        """.trimIndent()

        val response = callGeminiRaw(apiKey, prompt, null, brandKit)
        if (response.isSuccess) {
            val rawText = response.getOrNull() ?: ""
            Result.success(parseContentItemJson(rawText, "POSTS", platform, postType))
        } else {
            Result.success(generateLocalPost(topic, postType, platform, brandKit))
        }
    }

    suspend fun generateCaptions(
        apiKey: String,
        topic: String,
        platform: String,
        tone: String,
        length: String,
        brandKit: BrandKit?
    ): Result<List<String>> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext Result.success(generateLocalCaptions(topic, platform, tone, length, brandKit))
        }

        val prompt = """
            Write 3 distinct caption options for $platform about: '$topic'.
            Tone: $tone
            Length: $length

            Format strictly as JSON array of strings:
            [
              "Caption Option 1 with emojis and CTA",
              "Caption Option 2 with storytelling hook",
              "Caption Option 3 short punchy bullet points"
            ]
        """.trimIndent()

        val response = callGeminiRaw(apiKey, prompt, null, brandKit)
        if (response.isSuccess) {
            val rawText = response.getOrNull() ?: ""
            Result.success(parseJsonStringArray(rawText))
        } else {
            Result.success(generateLocalCaptions(topic, platform, tone, length, brandKit))
        }
    }

    suspend fun generateHashtags(
        apiKey: String,
        topic: String,
        platform: String,
        count: Int,
        brandKit: BrandKit?
    ): Result<HashtagGroups> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext Result.success(generateLocalHashtags(topic, platform, count, brandKit))
        }

        val prompt = """
            Generate $count hashtags for $platform related to: '$topic'.
            Categorize them strictly as JSON:
            {
              "highReach": ["#tag1", "#tag2", "#tag3"],
              "niche": ["#tag4", "#tag5", "#tag6"],
              "relevant": ["#tag7", "#tag8", "#tag9"]
            }
        """.trimIndent()

        val response = callGeminiRaw(apiKey, prompt, null, brandKit)
        if (response.isSuccess) {
            val rawText = response.getOrNull() ?: ""
            Result.success(parseHashtagGroups(rawText))
        } else {
            Result.success(generateLocalHashtags(topic, platform, count, brandKit))
        }
    }

    suspend fun generateCalendarPlan(
        apiKey: String,
        daysCount: Int,
        topic: String,
        brandKit: BrandKit?
    ): Result<List<CalendarEntryEntity>> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank()) {
            return@withContext Result.success(generateLocalCalendarPlan(daysCount, topic, brandKit))
        }

        val prompt = """
            Create a social media content calendar plan with $daysCount scheduled items starting from tomorrow.
            Focus/Topic: ${if (topic.isBlank()) "Audience growth & engagement" else topic}

            Format strictly as JSON array:
            [
              {
                "dayOffset": 1,
                "platform": "Instagram",
                "contentType": "Reel",
                "title": "Title of content",
                "status": "Ready"
              }
            ]
        """.trimIndent()

        val response = callGeminiRaw(apiKey, prompt, null, brandKit)
        if (response.isSuccess) {
            val rawText = response.getOrNull() ?: ""
            Result.success(parseCalendarEntries(rawText))
        } else {
            Result.success(generateLocalCalendarPlan(daysCount, topic, brandKit))
        }
    }

    private suspend fun callGeminiRaw(
        apiKey: String,
        prompt: String,
        imageBitmap: Bitmap?,
        brandKit: BrandKit?
    ): Result<String> {
        if (apiKey.isBlank()) {
            return Result.failure(Exception("Gemini API key is missing. Please configure it in Settings."))
        }

        try {
            val url = "$baseUrl?key=${apiKey.trim()}"
            val systemInstruction = buildSystemPrompt(brandKit)

            val requestJson = JSONObject().apply {
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", systemInstruction))
                    })
                })

                val contentsArray = JSONArray()
                val currentParts = JSONArray()
                currentParts.put(JSONObject().put("text", prompt))

                if (imageBitmap != null) {
                    val base64Data = bitmapToBase64(imageBitmap)
                    currentParts.put(JSONObject().apply {
                        put("inlineData", JSONObject().apply {
                            put("mimeType", "image/jpeg")
                            put("data", base64Data)
                        })
                    })
                }

                contentsArray.put(JSONObject().apply {
                    put("role", "user")
                    put("parts", currentParts)
                })

                put("contents", contentsArray)

                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.6)
                })
            }

            val request = Request.Builder()
                .url(url)
                .post(requestJson.toString().toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                val errorMsg = try {
                    val errJson = JSONObject(responseBody)
                    errJson.optJSONObject("error")?.optString("message")
                        ?: "API Error ${response.code}: $responseBody"
                } catch (e: Exception) {
                    "API Error ${response.code}: $responseBody"
                }
                return Result.failure(Exception(errorMsg))
            }

            val text = parseCandidateText(responseBody)
            return Result.success(text)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    private fun buildSystemPrompt(brandKit: BrandKit?): String {
        val base = """
            You are SaBuddy, a premier AI Social Media Manager.
            Your tone is FRIENDLY, SHARP, MODERN, and ACTIONABLE.
            You help creators and businesses generate viral hooks, scripts, captions, hashtags, and analyze screenshots of social media posts or analytics.
            Never sound overly corporate or robotic. Speak naturally like an experienced social media manager who cares about results.
        """.trimIndent()

        if (brandKit == null) return base

        return """
            $base
            
            USER BRAND KIT CONTEXT:
            - Brand Name: ${brandKit.brandName}
            - Niche: ${brandKit.niche}
            - Target Audience: ${brandKit.targetAudience}
            - Brand Voice: ${brandKit.brandVoice}
            - Tone: ${brandKit.tone}
            - Content Style: ${brandKit.contentStyle}
            - Content Goals: ${brandKit.contentGoals}
            - Do's: ${brandKit.dos}
            - Don'ts: ${brandKit.donts}

            Always incorporate this Brand Kit into your recommendations, scripts, and captions!
        """.trimIndent()
    }

    private fun parseCandidateText(responseBody: String): String {
        return try {
            val root = JSONObject(responseBody)
            val candidates = root.optJSONArray("candidates") ?: return "No response received."
            if (candidates.length() == 0) return "No response generated."
            val firstCandidate = candidates.getJSONObject(0)
            val content = firstCandidate.optJSONObject("content") ?: return "No content."
            val parts = content.optJSONArray("parts") ?: return "No text parts."
            val sb = StringBuilder()
            for (i in 0 until parts.length()) {
                val part = parts.getJSONObject(i)
                sb.append(part.optString("text", ""))
            }
            sb.toString().trim()
        } catch (e: Exception) {
            "Error parsing response: ${e.message}"
        }
    }

    private fun extractStructuredCard(text: String): StructuredAiCard? {
        return try {
            // Find JSON inside text if wrapped in ```json ... ``` or directly
            val jsonString = cleanJsonString(text)
            if (jsonString.startsWith("{") && jsonString.endsWith("}")) {
                val obj = JSONObject(jsonString)
                val title = obj.optString("title", "")
                val hook = obj.optString("hook", "")
                val cta = obj.optString("cta", "")
                val script = obj.optString("script", "")
                val body = obj.optString("body", "")
                val hashtags = obj.optString("hashtags", "")
                val type = when {
                    script.isNotBlank() -> "REEL_IDEA"
                    body.isNotBlank() -> "POST"
                    hashtags.isNotBlank() -> "HASHTAGS"
                    else -> "CAPTION"
                }
                if (title.isNotBlank() || hook.isNotBlank()) {
                    return StructuredAiCard(
                        type = type,
                        title = title.ifBlank { "Social Content Idea" },
                        hook = hook,
                        script = script,
                        cta = cta,
                        hashtags = hashtags,
                        body = body
                    )
                }
            }
            null
        } catch (e: Exception) {
            null
        }
    }

    private fun parseContentItemJson(
        rawText: String,
        category: String,
        platform: String,
        contentType: String
    ): ContentItemEntity {
        return try {
            val json = cleanJsonString(rawText)
            val obj = JSONObject(json)
            ContentItemEntity(
                category = category,
                title = obj.optString("title", "Untitled Content"),
                hook = obj.optString("hook", ""),
                body = obj.optString("body", ""),
                script = obj.optString("script", ""),
                caption = obj.optString("caption", ""),
                cta = obj.optString("cta", ""),
                hashtags = obj.optString("hashtags", ""),
                platform = platform,
                contentType = contentType,
                status = "Ready"
            )
        } catch (e: Exception) {
            ContentItemEntity(
                category = category,
                title = "Generated $category",
                hook = rawText.take(150),
                body = rawText,
                caption = rawText.take(250),
                platform = platform,
                contentType = contentType,
                status = "Ready"
            )
        }
    }

    private fun parseJsonStringArray(rawText: String): List<String> {
        return try {
            val json = cleanJsonString(rawText)
            val array = JSONArray(json)
            val list = mutableListOf<String>()
            for (i in 0 until array.length()) {
                list.add(array.getString(i))
            }
            list
        } catch (e: Exception) {
            rawText.split("\n\n").filter { it.isNotBlank() }
        }
    }

    private fun parseHashtagGroups(rawText: String): HashtagGroups {
        return try {
            val json = cleanJsonString(rawText)
            val obj = JSONObject(json)
            val highReach = toList(obj.optJSONArray("highReach"))
            val niche = toList(obj.optJSONArray("niche"))
            val relevant = toList(obj.optJSONArray("relevant"))
            HashtagGroups(highReach = highReach, niche = niche, relevant = relevant)
        } catch (e: Exception) {
            val words = rawText.split(Regex("[\\s,]+")).filter { it.startsWith("#") }
            HashtagGroups(
                highReach = words.take(4),
                niche = words.drop(4).take(4),
                relevant = words.drop(8)
            )
        }
    }

    private fun toList(array: JSONArray?): List<String> {
        if (array == null) return emptyList()
        val list = mutableListOf<String>()
        for (i in 0 until array.length()) {
            list.add(array.getString(i))
        }
        return list
    }

    private fun parseCalendarEntries(rawText: String): List<CalendarEntryEntity> {
        return try {
            val json = cleanJsonString(rawText)
            val array = JSONArray(json)
            val list = mutableListOf<CalendarEntryEntity>()
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val dayOffset = obj.optInt("dayOffset", i + 1)
                val targetDate = Date(System.currentTimeMillis() + (dayOffset * 86400000L))
                list.add(
                    CalendarEntryEntity(
                        date = sdf.format(targetDate),
                        platform = obj.optString("platform", "Instagram"),
                        contentType = obj.optString("contentType", "Reel"),
                        title = obj.optString("title", "Content piece $dayOffset"),
                        status = obj.optString("status", "Ready")
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun cleanJsonString(raw: String): String {
        var trimmed = raw.trim()
        if (trimmed.startsWith("```json")) {
            trimmed = trimmed.removePrefix("```json").trim()
        } else if (trimmed.startsWith("```")) {
            trimmed = trimmed.removePrefix("```").trim()
        }
        if (trimmed.endsWith("```")) {
            trimmed = trimmed.removeSuffix("```").trim()
        }
        val firstBrace = trimmed.indexOf('{')
        val firstBracket = trimmed.indexOf('[')
        if (firstBrace != -1 && (firstBracket == -1 || firstBrace < firstBracket)) {
            val lastBrace = trimmed.lastIndexOf('}')
            if (lastBrace > firstBrace) {
                return trimmed.substring(firstBrace, lastBrace + 1)
            }
        } else if (firstBracket != -1) {
            val lastBracket = trimmed.lastIndexOf(']')
            if (lastBracket > firstBracket) {
                return trimmed.substring(firstBracket, lastBracket + 1)
            }
        }
        return trimmed
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }

    // ==========================================
    // Local Built-in Creative Engine (No API Key)
    // ==========================================

    private fun generateLocalChatResponse(
        prompt: String,
        imageBitmap: Bitmap?,
        brandKit: BrandKit?,
        history: List<Pair<String, String>>
    ): Pair<String, StructuredAiCard?> {
        val p = prompt.lowercase()
        val niche = brandKit?.niche?.ifBlank { "Social Media & Content Strategy" } ?: "Social Media & Content Strategy"
        val brandName = brandKit?.brandName?.ifBlank { "your brand" } ?: "your brand"
        val tone = brandKit?.tone?.ifBlank { "Actionable & Engaging" } ?: "Actionable & Engaging"

        if (imageBitmap != null || p.contains("screenshot") || p.contains("audit") || p.contains("critique") || p.contains("analyze")) {
            val responseText = """
                Here is your visual & creative audit for $brandName ($niche):

                1. Visual Hierarchy & Contrast (8.8/10)
                • The primary visual focal point is clear, but text overlays risk blending into the background on high-refresh mobile screens.
                • Tweak: Place a 30% soft dark gradient under headline text or use a rounded backing tag to increase instant 0.3s scannability.

                2. Scroll-Stopping Hook
                • The opening message has good intent, but framing it with a high-curiosity or counter-intuitive trigger will increase view duration by 40%+.
                • Recommendation: Keep headline text to 6 words or fewer so it registers in the first eye-scan.

                3. Retention & Call To Action
                • Ensure interactive stickers, poll prompts, or CTA buttons sit comfortably within the 4:5 / 9:16 safe zone to prevent UI controls from clipping them.
            """.trimIndent()

            val card = StructuredAiCard(
                type = "REEL_IDEA",
                title = "Optimized Hook & Frame Structure",
                hook = "Stop making this mistake if you want to grow in $niche...",
                script = "[Visual: 0.5s snap zoom on visual asset] 'If you are struggling with engagement, here is the exact 1-tweak fix we implemented this week...'",
                cta = "Save this audit checklist for your next batch of posts!",
                hashtags = "#SocialMediaAudit #ContentStrategy #CreatorTips #GrowthHacks"
            )
            return Pair(responseText, card)
        }

        if (p.contains("reel") || p.contains("video") || p.contains("tiktok") || p.contains("short")) {
            val responseText = """
                Here's a high-retention 60-second video framework optimized for $niche:

                • 0:00-0:03s (Visual + Verbal Hook): Pattern interrupt that calls out a specific pain point.
                • 0:03-0:15s (Context): Briefly explain why standard advice fails.
                • 0:15-0:45s (Actionable Value): 2 specific, step-by-step tactics viewers can copy immediately.
                • 0:45-1:00s (Conversion Loop): Ask a binary question in the comments to drive algorithm momentum.
            """.trimIndent()

            val card = StructuredAiCard(
                type = "REEL_IDEA",
                title = "High-Retention Video Formula",
                hook = "The 60-second strategy that changed how I create in $niche",
                script = "[0-3s Visual: Quick text overlay with subtle sound effect]\n'Almost everyone gets this backwards.'\n\n[3-12s Context]\n'We spent 30 days testing different posting schedules and discovered this.'\n\n[12-40s Value Points]\n'Step 1: Focus on watch-time loops.\nStep 2: Use keyword-rich captions.'\n\n[40-60s CTA]\n'Comment 'GROWTH' and I'll send you our full step-by-step checklist!'",
                cta = "Save this script template for your next shoot day!",
                hashtags = "#ViralReels #CreatorEconomy #VideoMarketing #GrowthFormula"
            )
            return Pair(responseText, card)
        }

        if (p.contains("carousel") || p.contains("post") || p.contains("slides") || p.contains("linkedin")) {
            val responseText = """
                Here's a 6-slide high-conversion carousel structure for $brandName:

                • Slide 1: Bold provocative statement or unexpected data point.
                • Slide 2: The friction point everyone experiences.
                • Slide 3: The mindset shift or core framework.
                • Slide 4: Real-world walkthrough or case study.
                • Slide 5: Common mistake vs. recommended solution.
                • Slide 6: Summary checklist + prompt to save.
            """.trimIndent()

            val card = StructuredAiCard(
                type = "POST",
                title = "6-Slide Value Carousel",
                hook = "How to 10x your output in $niche without burning out",
                body = "Slide 1: The Blueprint Most People Miss\nSlide 2: Why Consistency Beats Perfection\nSlide 3: Our 3-Part Workflow\nSlide 4: Tools & Templates We Rely On\nSlide 5: 3 Traps To Avoid\nSlide 6: Save this carousel to execute tomorrow!",
                cta = "Bookmark this carousel and share it with your team.",
                hashtags = "#CarouselTips #LinkedInCreators #GrowthStrategy #BusinessTips"
            )
            return Pair(responseText, card)
        }

        if (p.contains("caption") || p.contains("hook")) {
            val responseText = """
                Here are 3 high-converting caption angles for $brandName ($tone tone):

                Angle 1: Short & Punchy (High comments)
                Angle 2: Storytelling (High shares & brand affinity)
                Angle 3: Step-by-Step Checklist (High saves)
            """.trimIndent()

            val card = StructuredAiCard(
                type = "CAPTION",
                title = "High-Converting Caption Kit",
                hook = "Most people won't tell you this about $niche, but...",
                body = "Here is what happens when you prioritize substance over vanity metrics:\n\n1. Better community trust\n2. Higher inbound inquiries\n3. Effortless content repurposing\n\nDrop a comment below: What is your #1 goal this month?",
                cta = "Save this post and comment your thoughts below!",
                hashtags = "#ContentCreators #SocialStrategy #AuthenticGrowth"
            )
            return Pair(responseText, card)
        }

        if (p.contains("tag") || p.contains("hashtag")) {
            val responseText = """
                Here is an optimized 3-tier hashtag strategy for $niche:
                • High Reach (Broad industry traffic)
                • Targeted Niche (Engaged relevant community)
                • Community & Topical (High conversion search terms)
            """.trimIndent()

            val card = StructuredAiCard(
                type = "HASHTAGS",
                title = "Categorized Hashtag Cluster",
                hook = "3-Tier Hashtag Matrix for $niche",
                hashtags = "#$niche #CreatorTips #SocialStrategy #OrganicGrowth #BuildInPublic #ContentMarketing",
                cta = "Copy and rotate these tags in your upcoming posts!"
            )
            return Pair(responseText, card)
        }

        // General assistance
        val responseText = """
            Hey! SaBuddy is running in local creative assistant mode for $brandName.

            Here are 3 quick actions we can take right now:
            1. Create a 60-second viral Reel script for $niche
            2. Draft a high-engagement 6-slide carousel or LinkedIn post
            3. Generate 3 distinct caption styles with hooks & hashtags

            What would you like to build first?
        """.trimIndent()

        val card = StructuredAiCard(
            type = "POST",
            title = "Daily Social Media Quick-Win",
            hook = "3 habits that separate amateur creators from pros in $niche",
            body = "1. Batch creation on fixed days\n2. Hook testing before filming\n3. Engaging with your top 10 community members daily",
            cta = "Save this reminder for your morning routine!",
            hashtags = "#CreatorMindset #ProductivityHacks #DailyConsistency"
        )
        return Pair(responseText, card)
    }

    private fun generateLocalIdea(
        topic: String,
        platform: String,
        contentType: String,
        brandKit: BrandKit?
    ): ContentItemEntity {
        val t = topic.ifBlank { "High-value strategies in our niche" }
        val brand = brandKit?.brandName?.ifBlank { "Our Brand" } ?: "Our Brand"
        val niche = brandKit?.niche?.ifBlank { "Social Media" } ?: "Social Media"

        val title = "The 80/20 Rule For $t"
        val hook = "Stop spending hours on $t. Here is what actually moves the needle in 2026."
        val body = """
            • Point 1: The #1 misconception about $t and why it holds creators back.
            • Point 2: The simple 3-step system $brand uses to generate consistent results.
            • Point 3: How to automate and repurpose this across $platform in under 30 minutes.
        """.trimIndent()
        val caption = """
            If you've been feeling stuck with $t, you're not alone.
            
            Most advice overcomplicates the process. By focusing exclusively on the 20% of inputs that produce 80% of your reach, you can build steady momentum without burning out.
            
            Which of these points resonated with you most? Let's discuss in the comments!
        """.trimIndent()
        val cta = "Save this post and share it with someone who needs this reminder today!"
        val cleanTopicTag = t.split(" ").firstOrNull()?.replace(Regex("[^a-zA-Z0-9]"), "") ?: "Creator"
        val hashtags = "#$cleanTopicTag #$niche #CreatorTips #OrganicGrowth #SocialStrategy #ViralContent"

        return ContentItemEntity(
            category = "IDEAS",
            title = title,
            hook = hook,
            body = body,
            caption = caption,
            cta = cta,
            hashtags = hashtags,
            platform = platform,
            contentType = contentType,
            status = "Ready"
        )
    }

    private fun generateLocalReel(
        topic: String,
        brandKit: BrandKit?
    ): ContentItemEntity {
        val t = topic.ifBlank { "growing your reach organically" }
        val niche = brandKit?.niche?.ifBlank { "Creator" } ?: "Creator"

        val title = "The 30-Second Fix for $t"
        val hook = "[Visual: Pointing to text overlay with audio beat] 'Nobody is talking about this $t secret...'"
        val script = """
            [0:00 - 0:03] Hook:
            Visual snap-zoom. 'If you want to master $t, stop doing this one thing.'
            
            [0:03 - 0:12] The Core Friction:
            'Most people focus on fancy editing or algorithm tricks. But watch-time always comes down to opening clarity.'
            
            [0:12 - 0:35] The 2-Step Framework:
            'Step 1: Deliver your payoff promise within the first 4 seconds.
            Step 2: Use bold on-screen captions so silent scrollers stay hooked.'
            
            [0:35 - 0:48] Real Example:
            'Notice how this video kept your attention without complex transitions? That is the power of simple structure.'
            
            [0:48 - 1:00] Call To Action:
            'Comment 'TEMPLATE' below and I'll send you our 5 favorite video hooks for $niche!'
        """.trimIndent()
        val caption = """
            The single biggest mistake we see with $t? Overcomplicating the hook.
            
            Save this reel and try this exact formula on your next post!
        """.trimIndent()
        val cta = "Comment 'TEMPLATE' below for the free hook formula!"
        val hashtags = "#ReelsTips #ViralReels #CreatorHacks #VideoStrategy #$niche"

        return ContentItemEntity(
            category = "REELS",
            title = title,
            hook = hook,
            script = script,
            caption = caption,
            cta = cta,
            hashtags = hashtags,
            platform = "Instagram",
            contentType = "Reel",
            status = "Ready"
        )
    }

    private fun generateLocalPost(
        topic: String,
        postType: String,
        platform: String,
        brandKit: BrandKit?
    ): ContentItemEntity {
        val t = topic.ifBlank { "Lessons from scaling content" }
        val niche = brandKit?.niche?.ifBlank { "Social Media" } ?: "Social Media"

        val isCarousel = postType.contains("Carousel", ignoreCase = true)
        val title = if (isCarousel) "The $t Playbook (Swipeable Breakdown)" else "A Deep Dive Into $t"
        val hook = "Everything you need to know about $t in 6 simple slides."
        val body = if (isCarousel) {
            """
                Slide 1 (Cover): The Ultimate Cheat Sheet for $t
                Slide 2 (The Problem): Why 90% of approaches fall flat
                Slide 3 (The Core Principle): The shift that changes the outcome
                Slide 4 (Step-by-Step): Exactly how to execute in 3 steps
                Slide 5 (The Shortcut): Tools and workflows that save 5+ hours weekly
                Slide 6 (Summary & CTA): Save this carousel for your next review session!
            """.trimIndent()
        } else {
            """
                Here are 3 key takeaways about $t:
                
                1. Clarity always outperforms complexity.
                2. Consistency in testing reveals what your audience genuinely values.
                3. Direct calls to action turn casual viewers into long-term followers.
            """.trimIndent()
        }
        val caption = "We packaged our entire framework for $t into this breakdown. Save and share with your team!"
        val cta = "Save this post to revisit during your next strategy session."
        val hashtags = "#$niche #BusinessGrowth #CreatorPlaybook #StrategyTips"

        return ContentItemEntity(
            category = "POSTS",
            title = title,
            hook = hook,
            body = body,
            caption = caption,
            cta = cta,
            hashtags = hashtags,
            platform = platform,
            contentType = postType,
            status = "Ready"
        )
    }

    private fun generateLocalCaptions(
        topic: String,
        platform: String,
        tone: String,
        length: String,
        brandKit: BrandKit?
    ): List<String> {
        val t = topic.ifBlank { "our latest project and lessons learned" }
        val niche = brandKit?.niche?.ifBlank { "Creator" } ?: "Creator"

        val cap1 = """
            The honest truth about $t that nobody talks about 👇
            
            When we first started exploring this, we thought more effort equaled better results. We were wrong.
            
            Here is the 1 shift that transformed everything for our community in $niche:
            Focusing on the first 5 seconds of value delivery.
            
            Double tap if you agree, and tell me: What's your biggest challenge with $t?
        """.trimIndent()

        val cap2 = """
            Save this before your next batch: 3 rules for $t 📌
            
            1. Keep it clear, not clever.
            2. Optimize for bookmarks and saves.
            3. End with a specific conversation question.
            
            Which of these three do you need to focus on this week? Drop a comment below!
        """.trimIndent()

        val cap3 = """
            Unpopular opinion: You don't need complicated tools to master $t.
            
            You just need a repeatable system and clear feedback loops.
            
            Bookmark this for your Monday planning session!
        """.trimIndent()

        return listOf(cap1, cap2, cap3)
    }

    private fun generateLocalHashtags(
        topic: String,
        platform: String,
        count: Int,
        brandKit: BrandKit?
    ): HashtagGroups {
        val clean = topic.split(" ").map { it.replace(Regex("[^a-zA-Z0-9]"), "") }.filter { it.isNotBlank() }
        val primary = clean.firstOrNull() ?: "Creator"
        val niche = brandKit?.niche?.replace(" ", "")?.replace(Regex("[^a-zA-Z0-9]"), "") ?: "SocialMedia"

        val highReach = listOf("#ContentCreator", "#SocialMediaTips", "#CreatorEconomy", "#ViralGrowth", "#DigitalMarketing")
        val nicheTags = listOf("#$primary", "#${primary}Tips", "#$niche", "#${niche}Strategy", "#CreatorHacks")
        val relevant = listOf("#BuildInPublic", "#DailyContent", "#EngagementTips", "#OrganicReach", "#ContentPlanning")

        return HashtagGroups(highReach = highReach, niche = nicheTags, relevant = relevant)
    }

    private fun generateLocalCalendarPlan(
        daysCount: Int,
        topic: String,
        brandKit: BrandKit?
    ): List<CalendarEntryEntity> {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val list = mutableListOf<CalendarEntryEntity>()
        val platforms = listOf("Instagram", "LinkedIn", "TikTok", "X", "YouTube Shorts")
        val contentTypes = listOf("Reel", "Carousel", "Story", "Text Post", "Short")
        val titles = listOf(
            "The 60-Second $topic Blueprint",
            "3 Costly Mistakes with $topic (And The Fix)",
            "Behind The Scenes: How We Test $topic",
            "Community Q&A: Answering Your Top Questions",
            "Case Study: What Worked This Week",
            "Weekend Mindset: 1 Golden Rule for $topic",
            "Monday Kickoff: The 7-Day $topic Challenge"
        )

        val days = daysCount.coerceIn(1, 14)
        for (i in 0 until days) {
            val targetDate = Date(System.currentTimeMillis() + ((i + 1) * 86400000L))
            list.add(
                CalendarEntryEntity(
                    date = sdf.format(targetDate),
                    platform = platforms[i % platforms.size],
                    contentType = contentTypes[i % contentTypes.size],
                    title = titles[i % titles.size],
                    status = "Ready"
                )
            )
        }
        return list
    }
}
