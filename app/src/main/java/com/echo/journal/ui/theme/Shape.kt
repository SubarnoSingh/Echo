package com.echo.journal.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

object EchoRadius {
    val xs = 8.dp
    val sm = 12.dp
    val md = 16.dp
    val card = 24.dp
    val panel = 28.dp
    val hero = 32.dp
    val nav = 34.dp
    val pill = 100.dp
}

object EchoSpacing {
    val xxs = 4.dp
    val xs = 8.dp
    val sm = 12.dp
    val md = 16.dp
    val lg = 20.dp
    val xl = 24.dp
    val xxl = 32.dp
    val xxxl = 40.dp
    val huge = 56.dp
}

val EchoShapes = Shapes(
    extraSmall = RoundedCornerShape(EchoRadius.xs),
    small = RoundedCornerShape(EchoRadius.sm),
    medium = RoundedCornerShape(EchoRadius.md),
    large = RoundedCornerShape(EchoRadius.card),
    extraLarge = RoundedCornerShape(EchoRadius.panel),
)
