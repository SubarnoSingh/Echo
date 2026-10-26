package com.echo.journal.domain

/**
 * The emotional vocabulary Echo offers. Moods are intentionally descriptive rather than
 * clinical. [key] is the stable value persisted in Room.
 */
enum class Mood(
    val key: String,
    val label: String,
    val emoji: String,
) {
    HAPPY("happy", "Happy", "\uD83D\uDE0A"),
    CALM("calm", "Calm", "\uD83D\uDE0C"),
    GRATEFUL("grateful", "Grateful", "\uD83E\uDD79"),
    NEUTRAL("neutral", "Neutral", "\uD83D\uDE10"),
    ANXIOUS("anxious", "Anxious", "\uD83D\uDE1F"),
    SAD("sad", "Sad", "\uD83D\uDE14"),
    FRUSTRATED("frustrated", "Frustrated", "\uD83D\uDE24"),
    TIRED("tired", "Tired", "\uD83D\uDE34");

    companion object {
        fun fromKey(key: String?): Mood? =
            key?.let { stored -> entries.firstOrNull { it.key == stored } }
    }
}
