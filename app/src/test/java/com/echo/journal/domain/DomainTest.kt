package com.echo.journal.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

class DomainTest {
    private val zone = ZoneId.of("UTC")

    private fun at(
        day: Int,
        hour: Int = 10,
        month: Int = 10,
        year: Int = 2026,
    ): Long = ZonedDateTime.of(year, month, day, hour, 0, 0, 0, zone)
        .toInstant()
        .toEpochMilli()

    private fun entry(
        id: Long = 1,
        title: String = "",
        body: String = "",
        mood: Mood? = null,
        createdAt: Long = at(26),
    ) = JournalEntry(
        id = id,
        title = title,
        body = body,
        mood = mood,
        createdAt = createdAt,
        updatedAt = createdAt,
    )

    private fun checkIn(
        id: Long = 1,
        mood: Mood = Mood.CALM,
        createdAt: Long = at(26),
        note: String = "",
    ) = MoodCheckIn(id, mood, note, createdAt)

    @Test
    fun moodStorageRoundTripsAndRejectsUnknownKeys() {
        Mood.entries.forEach { mood ->
            assertEquals(mood, Mood.fromKey(mood.key))
        }
        assertNull(Mood.fromKey("melancholy"))
        assertNull(Mood.fromKey(null))
    }

    @Test
    fun displayTitleFallsBackToFirstBodyLine() {
        assertEquals("A named page", entry(title = "A named page", body = "Body").displayTitle)
        assertEquals("The body speaks", entry(body = "The body speaks\nMore words").displayTitle)
        assertEquals("Untitled echo", entry().displayTitle)
    }

    @Test
    fun displayTitleAndPreviewAreTruncated() {
        val long = "a".repeat(200)
        val e = entry(body = long)
        assertTrue(e.displayTitle.length <= 54)
        assertTrue(e.displayTitle.endsWith("\u2026"))
        assertTrue(e.preview.length <= 150)
        assertTrue(e.preview.endsWith("\u2026"))
    }

    @Test
    fun searchMatchesTitleBodyAndMoodIgnoringCase() {
        val entries = listOf(
            entry(id = 1, title = "Morning walk", body = "The air was cool", mood = Mood.CALM),
            entry(id = 2, title = "A hard day", body = "Too many meetings", mood = Mood.TIRED),
        )
        assertEquals(listOf(1L), filterJournalEntries(entries, "WALK").map { it.id })
        assertEquals(listOf(1L), filterJournalEntries(entries, "cool").map { it.id })
        assertEquals(listOf(2L), filterJournalEntries(entries, "tired").map { it.id })
        assertEquals(2, filterJournalEntries(entries, "  ").size)
        assertTrue(filterJournalEntries(entries, "missing").isEmpty())
    }

    @Test
    fun insightsReflectOnlyRecordedData() {
        val entries = listOf(
            entry(id = 1, createdAt = at(25)),
            entry(id = 2, createdAt = at(26)),
        )
        val checkIns = listOf(
            checkIn(id = 1, mood = Mood.CALM, createdAt = at(20)),
            checkIn(id = 2, mood = Mood.CALM, createdAt = at(21)),
            checkIn(id = 3, mood = Mood.HAPPY, createdAt = at(26)),
            checkIn(id = 4, mood = Mood.TIRED, createdAt = at(26, hour = 18)),
            checkIn(id = 5, mood = Mood.SAD, createdAt = at(18)),
        )

        val summary = buildInsights(entries, checkIns, now = at(26, hour = 20), zone = zone)

        assertEquals(2, summary.totalEntries)
        assertEquals(5, summary.totalCheckIns)
        assertEquals(5, summary.recentCheckInCount)
        assertEquals(Mood.CALM, summary.topMood)
        assertEquals(
            listOf(
                Mood.CALM to 2,
                Mood.HAPPY to 1,
                Mood.SAD to 1,
                Mood.TIRED to 1,
            ),
            summary.distribution.map { it.mood to it.count },
        )
        assertEquals(4, summary.checkInsLast7Days)
        assertEquals(1, summary.checkInsPrevious7Days)
        assertEquals(3, summary.trendDelta)
        assertEquals(3, summary.checkInDaysLast7)
        assertEquals(2, summary.journalDaysLast7)
        assertEquals(1, summary.currentStreak)
        assertEquals(7, summary.weekMoods.size)
        assertEquals(Mood.TIRED, summary.weekMoods.last().mood)
    }

    @Test
    fun oldCheckInsAppearInTotalsButNotRecentDistribution() {
        val summary = buildInsights(
            entries = listOf(entry(createdAt = at(1, month = 1))),
            checkIns = listOf(
                checkIn(createdAt = at(1, month = 1)),
                checkIn(createdAt = at(2, month = 1)),
            ),
            now = at(26),
            zone = zone,
        )
        assertEquals(2, summary.totalCheckIns)
        assertEquals(0, summary.recentCheckInCount)
        assertNull(summary.topMood)
        assertTrue(summary.distribution.isEmpty())
    }

    @Test
    fun streakContinuesFromYesterdayButNotFromTwoDaysAgo() {
        val today = LocalDate.of(2026, 10, 26)
        assertEquals(
            3,
            currentStreak(setOf(today, today.minusDays(1), today.minusDays(2)), today),
        )
        assertEquals(
            2,
            currentStreak(setOf(today.minusDays(1), today.minusDays(2)), today),
        )
        assertEquals(
            0,
            currentStreak(setOf(today.minusDays(2), today.minusDays(3)), today),
        )
        assertEquals(0, currentStreak(emptySet(), today))
    }

    @Test
    fun homeSnapshotUsesTodayAndLastSevenDays() {
        val entries = listOf(
            entry(id = 1, createdAt = at(26, hour = 9)),
            entry(id = 2, createdAt = at(10)),
        )
        val checkIns = listOf(
            checkIn(id = 1, mood = Mood.HAPPY, createdAt = at(26, hour = 8)),
            checkIn(id = 2, mood = Mood.CALM, createdAt = at(26, hour = 19)),
            checkIn(id = 3, mood = Mood.SAD, createdAt = at(25, hour = 12)),
        )

        val snapshot = buildHomeSnapshot(entries, checkIns, now = at(26, hour = 20), zone = zone)

        assertEquals(2, snapshot.checkInsToday)
        assertEquals(Mood.CALM, snapshot.latestToday?.mood)
        assertEquals(1, snapshot.entriesThisWeek)
        assertEquals(2, snapshot.currentStreak)
        assertEquals(2, snapshot.totalEntries)
        assertEquals(3, snapshot.totalCheckIns)
    }

    @Test
    fun wordCountsIgnoreWhitespaceAndPluralize() {
        assertEquals(0, wordCount("   \n  "))
        assertEquals(1, wordCount("  alone  "))
        assertEquals(4, wordCount("one\ttwo\nthree   four"))
        assertEquals("0 words", wordCountLabel(""))
        assertEquals("1 word", wordCountLabel("Alone"))
        assertEquals("5 words", wordCountLabel("Five words live right here"))
    }

    @Test
    fun dailyPromptIsStableForTheDayAndVariesAcrossDays() {
        val date = LocalDate.of(2026, 10, 26)
        assertEquals(dailyPrompt(date), dailyPrompt(date))
        val prompts = (0..30).map { dailyPrompt(date.plusDays(it.toLong())) }.toSet()
        assertTrue(prompts.size > 1)
    }
}
