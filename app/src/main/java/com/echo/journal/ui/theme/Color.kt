package com.echo.journal.ui.theme

import androidx.compose.ui.graphics.Color

// Core nocturnal palette. All UI colors flow from this file.
val EchoNight = Color(0xFF0B0B10)
val EchoNightSoft = Color(0xFF12121B)
val EchoElevated = Color(0xFF191925)
val EchoGlassHighlight = Color(0xFFFFFFFF)
val EchoAccent = Color(0xFFA78BFA)
val EchoAccentSoft = Color(0xFFC4B5FD)
val EchoAccentDeep = Color(0xFF7C5CE0)
val EchoTextPrimary = Color(0xFFF7F5FF)
val EchoTextMuted = Color(0xFFA1A1B5)
val EchoTextFaint = Color(0xFF6E6E82)
val EchoBorder = Color(0x1AFFFFFF)
val EchoSuccess = Color(0xFF8FDDBE)
val EchoWarning = Color(0xFFEBC98A)
val EchoDanger = Color(0xFFF0A0A0)

// Ambient light used by the atmospheric canvas.
val EchoGlowViolet = Color(0xFF7C5CE0)
val EchoGlowLilac = Color(0xFFC4B5FD)
val EchoGlowIndigo = Color(0xFF4338CA)
val EchoGlowRose = Color(0xFF9D5C9E)

fun moodColor(key: String): Color = when (key) {
    "happy" -> Color(0xFFF1C97B)
    "calm" -> Color(0xFF83D7CA)
    "grateful" -> Color(0xFFE3AEED)
    "neutral" -> Color(0xFFB4B4C7)
    "anxious" -> Color(0xFFEDB978)
    "sad" -> Color(0xFF8EB4EC)
    "frustrated" -> Color(0xFFEE9494)
    "tired" -> Color(0xFFA6A1CF)
    else -> EchoAccentSoft
}
