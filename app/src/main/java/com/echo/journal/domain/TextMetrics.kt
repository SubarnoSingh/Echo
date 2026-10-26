package com.echo.journal.domain

fun wordCount(text: String): Int {
    val normalized = text.trim()
    if (normalized.isEmpty()) return 0
    return normalized.split(Regex("\\s+")).count { it.isNotBlank() }
}

fun wordCountLabel(text: String): String {
    val count = wordCount(text)
    return if (count == 1) "1 word" else "$count words"
}
