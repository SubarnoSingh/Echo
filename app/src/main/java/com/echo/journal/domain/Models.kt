package com.echo.journal.domain

data class JournalEntry(
    val id: Long,
    val title: String,
    val body: String,
    val mood: Mood?,
    val createdAt: Long,
    val updatedAt: Long,
) {
    val displayTitle: String
        get() {
            val explicit = title.trim()
            if (explicit.isNotEmpty()) return explicit
            val firstLine = body.lineSequence()
                .map { it.trim() }
                .firstOrNull { it.isNotEmpty() }
                ?: return "Untitled echo"
            return if (firstLine.length <= 54) firstLine else firstLine.take(53).trimEnd() + "\u2026"
        }

    val preview: String
        get() {
            val collapsed = body.trim().replace(Regex("\\s+"), " ")
            return if (collapsed.length <= 150) collapsed else collapsed.take(149).trimEnd() + "\u2026"
        }
}

data class MoodCheckIn(
    val id: Long,
    val mood: Mood,
    val note: String,
    val createdAt: Long,
)
