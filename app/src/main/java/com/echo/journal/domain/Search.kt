package com.echo.journal.domain

/** Case-insensitive search across title, body and mood label. */
fun filterJournalEntries(
    entries: List<JournalEntry>,
    query: String,
): List<JournalEntry> {
    val needle = query.trim()
    if (needle.isEmpty()) return entries
    return entries.filter { entry ->
        entry.title.contains(needle, ignoreCase = true) ||
            entry.body.contains(needle, ignoreCase = true) ||
            entry.mood?.label?.contains(needle, ignoreCase = true) == true
    }
}
