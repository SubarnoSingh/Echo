package com.echo.journal.domain

import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Locale

private val dayMillis = 24 * 60 * 60 * 1000L

fun greetingFor(hour: Int): String = when (hour) {
    in 5..11 -> "Good morning"
    in 12..16 -> "Good afternoon"
    in 17..21 -> "Good evening"
    else -> "Still awake?"
}

fun todayLongLabel(date: LocalDate = LocalDate.now()): String =
    date.format(DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale.getDefault()))

fun shortDateLabel(millis: Long, zone: ZoneId = ZoneId.systemDefault()): String =
    Instant.ofEpochMilli(millis)
        .atZone(zone)
        .format(DateTimeFormatter.ofPattern("d MMM yyyy", Locale.getDefault()))

fun relativeDateLabel(
    millis: Long,
    now: Long = System.currentTimeMillis(),
    zone: ZoneId = ZoneId.systemDefault(),
): String {
    val date = Instant.ofEpochMilli(millis).atZone(zone).toLocalDate()
    val today = Instant.ofEpochMilli(now).atZone(zone).toLocalDate()
    val time = Instant.ofEpochMilli(millis)
        .atZone(zone)
        .format(DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault()))
    return when {
        date == today -> "Today, $time"
        date == today.minusDays(1) -> "Yesterday, $time"
        ChronoUnit.DAYS.between(date, today) < 7 ->
            "${date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())}, $time"
        else -> date.format(DateTimeFormatter.ofPattern("d MMM yyyy", Locale.getDefault()))
    }
}

fun timeLabel(millis: Long, zone: ZoneId = ZoneId.systemDefault()): String =
    Instant.ofEpochMilli(millis)
        .atZone(zone)
        .format(DateTimeFormatter.ofPattern("h:mm a", Locale.getDefault()))

fun editorDateLabel(millis: Long, zone: ZoneId = ZoneId.systemDefault()): String =
    Instant.ofEpochMilli(millis)
        .atZone(zone)
        .format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy \u00b7 h:mm a", Locale.getDefault()))

fun dayKey(millis: Long, zone: ZoneId = ZoneId.systemDefault()): LocalDate =
    Instant.ofEpochMilli(millis).atZone(zone).toLocalDate()

fun startOfDayMillis(date: LocalDate, zone: ZoneId = ZoneId.systemDefault()): Long =
    date.atStartOfDay(zone).toInstant().toEpochMilli()

/** Days since the epoch, useful for lightweight calendar maths. */
fun LocalDate.epochDayLong(): Long = toEpochDay()

fun isSameWeek(date: LocalDate, reference: LocalDate): Boolean {
    val start = reference.with(DayOfWeek.MONDAY)
    return !date.isBefore(start) && date.isBefore(start.plusDays(7))
}

fun localDateTime(millis: Long, zone: ZoneId = ZoneId.systemDefault()): LocalDateTime =
    Instant.ofEpochMilli(millis).atZone(zone).toLocalDateTime()

internal fun daysAgoMillis(days: Long, now: Long, zone: ZoneId): Long =
    startOfDayMillis(dayKey(now, zone).minusDays(days), zone)

internal val oneDayMillis: Long = dayMillis
