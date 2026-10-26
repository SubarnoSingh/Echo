package com.echo.journal.domain

import java.time.LocalDate
import java.time.ZoneId

data class MoodCount(
    val mood: Mood,
    val count: Int,
)

data class DayMood(
    val date: LocalDate,
    val mood: Mood?,
    val checkInCount: Int,
)

data class InsightsSummary(
    val totalEntries: Int = 0,
    val totalCheckIns: Int = 0,
    val recentCheckInCount: Int = 0,
    val topMood: Mood? = null,
    val distribution: List<MoodCount> = emptyList(),
    val weekMoods: List<DayMood> = emptyList(),
    val checkInsLast7Days: Int = 0,
    val checkInsPrevious7Days: Int = 0,
    val checkInDaysLast7: Int = 0,
    val journalDaysLast7: Int = 0,
    val currentStreak: Int = 0,
    val recentCheckIns: List<MoodCheckIn> = emptyList(),
) {
    val trendDelta: Int get() = checkInsLast7Days - checkInsPrevious7Days

    val hasMoodData: Boolean get() = totalCheckIns > 0
    val hasRecentMoodData: Boolean get() = recentCheckInCount > 0
}

data class HomeSnapshot(
    val totalEntries: Int = 0,
    val entriesThisWeek: Int = 0,
    val checkInsToday: Int = 0,
    val latestToday: MoodCheckIn? = null,
    val currentStreak: Int = 0,
    val totalCheckIns: Int = 0,
)

/** Groups all recorded check-ins within the rolling 30-day window into a ranked distribution. */
fun buildInsights(
    entries: List<JournalEntry>,
    checkIns: List<MoodCheckIn>,
    now: Long = System.currentTimeMillis(),
    zone: ZoneId = ZoneId.systemDefault(),
): InsightsSummary {
    val today = dayKey(now, zone)
    val windowStart = startOfDayMillis(today.minusDays(29), zone)
    val weekStart = startOfDayMillis(today.minusDays(6), zone)
    val previousWeekStart = startOfDayMillis(today.minusDays(13), zone)
    val recent = checkIns.filter { it.createdAt >= windowStart }

    val distribution = recent
        .groupingBy { it.mood }
        .eachCount()
        .map { (mood, count) -> MoodCount(mood, count) }
        .sortedWith(compareByDescending<MoodCount> { it.count }.thenBy { it.mood.ordinal })

    val checkInsLast7 = checkIns.count { it.createdAt >= weekStart }
    val checkInsPrevious7 = checkIns.count {
        it.createdAt >= previousWeekStart && it.createdAt < weekStart
    }

    val checkInDays = checkIns
        .map { dayKey(it.createdAt, zone) }
        .toSet()
    val entriesDays = entries
        .filter { it.createdAt >= weekStart }
        .map { dayKey(it.createdAt, zone) }
        .toSet()

    val weekMoods = (6L downTo 0L).map { offset ->
        val date = today.minusDays(offset)
        val moods = checkIns.filter { dayKey(it.createdAt, zone) == date }
        DayMood(
            date = date,
            mood = moods.maxByOrNull { it.createdAt }?.mood,
            checkInCount = moods.size,
        )
    }

    return InsightsSummary(
        totalEntries = entries.size,
        totalCheckIns = checkIns.size,
        recentCheckInCount = recent.size,
        topMood = distribution.firstOrNull()?.mood,
        distribution = distribution,
        weekMoods = weekMoods,
        checkInsLast7Days = checkInsLast7,
        checkInsPrevious7Days = checkInsPrevious7,
        checkInDaysLast7 = checkInDays.count { it >= today.minusDays(6) },
        journalDaysLast7 = entriesDays.size,
        currentStreak = currentStreak(checkInDays, today),
        recentCheckIns = checkIns.sortedByDescending { it.createdAt }.take(6),
    )
}

fun currentStreak(
    checkInDays: Set<LocalDate>,
    today: LocalDate,
): Int {
    var cursor = when {
        today in checkInDays -> today
        today.minusDays(1) in checkInDays -> today.minusDays(1)
        else -> return 0
    }
    var streak = 0
    while (cursor in checkInDays) {
        streak++
        cursor = cursor.minusDays(1)
    }
    return streak
}

fun buildHomeSnapshot(
    entries: List<JournalEntry>,
    checkIns: List<MoodCheckIn>,
    now: Long = System.currentTimeMillis(),
    zone: ZoneId = ZoneId.systemDefault(),
): HomeSnapshot {
    val today = dayKey(now, zone)
    val weekStart = startOfDayMillis(today.minusDays(6), zone)
    val todaysCheckIns = checkIns.filter { dayKey(it.createdAt, zone) == today }
    val checkInDays = checkIns.map { dayKey(it.createdAt, zone) }.toSet()
    return HomeSnapshot(
        totalEntries = entries.size,
        entriesThisWeek = entries.count { it.createdAt >= weekStart },
        checkInsToday = todaysCheckIns.size,
        latestToday = todaysCheckIns.maxByOrNull { it.createdAt },
        currentStreak = currentStreak(checkInDays, today),
        totalCheckIns = checkIns.size,
    )
}

private val reflectionPrompts = listOf(
    "What felt lighter today?",
    "What is one thing you want to remember about today?",
    "Where did you notice a moment of quiet?",
    "What are you carrying that you could set down?",
    "What made you feel most like yourself today?",
    "What would you tell yourself one week ago?",
    "What is something small you are looking forward to?",
    "Which moment today deserves a second look?",
    "What did your body try to tell you today?",
    "What are you grateful for, without overthinking it?",
)

fun dailyPrompt(date: LocalDate = LocalDate.now()): String =
    reflectionPrompts[Math.floorMod(date.dayOfYear - 1, reflectionPrompts.size)]
