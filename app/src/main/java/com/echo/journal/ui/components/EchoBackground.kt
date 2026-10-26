package com.echo.journal.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.echo.journal.ui.theme.EchoGlowIndigo
import com.echo.journal.ui.theme.EchoGlowLilac
import com.echo.journal.ui.theme.EchoGlowRose
import com.echo.journal.ui.theme.EchoGlowViolet
import com.echo.journal.ui.theme.EchoNight
import com.echo.journal.ui.theme.LocalReduceMotion

/**
 * Shared atmospheric backdrop. The light is painted with large, low-alpha radial gradients rather
 * than blurred layers: soft edges come for free and there is no per-frame blur cost. Motion is a
 * slow drift between two compositions, and is disabled when the user removes system animations.
 *
 * This is a deliberate approximation of frosted glass. Android has no public API that lets an
 * ordinary composable sample and blur the pixels painted behind it, so glass surfaces elsewhere
 * lean on translucency, internal light and the light from this canvas.
 */
@Composable
fun EchoBackground(modifier: Modifier = Modifier) {
    val reduceMotion = LocalReduceMotion.current
    val phase = if (reduceMotion) {
        0.45f
    } else {
        val transition = rememberInfiniteTransition(label = "ambient-light")
        val drift by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 52_000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse,
            ),
            label = "ambient-drift",
        )
        drift
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val travel = (phase - 0.5f) * 2f

        drawRect(
            Brush.verticalGradient(
                0f to Color(0xFF08080D),
                0.45f to EchoNight,
                1f to Color(0xFF0D0917),
            ),
        )

        // Violet bloom, upper right.
        drawRect(
            Brush.radialGradient(
                colors = listOf(
                    EchoGlowViolet.copy(alpha = 0.26f),
                    EchoGlowViolet.copy(alpha = 0.08f),
                    Color.Transparent,
                ),
                center = Offset(w * (0.86f + travel * 0.05f), h * (0.07f + travel * 0.02f)),
                radius = w * 1.02f,
            ),
        )

        // Indigo depth on the left.
        drawRect(
            Brush.radialGradient(
                colors = listOf(
                    EchoGlowIndigo.copy(alpha = 0.16f),
                    EchoGlowIndigo.copy(alpha = 0.04f),
                    Color.Transparent,
                ),
                center = Offset(w * (-0.18f - travel * 0.03f), h * 0.46f),
                radius = w * 0.9f,
            ),
        )

        // A warm lilac haze low on the screen, kept very quiet.
        drawRect(
            Brush.radialGradient(
                colors = listOf(
                    EchoGlowLilac.copy(alpha = 0.075f),
                    Color.Transparent,
                ),
                center = Offset(w * (0.38f - travel * 0.04f), h * (0.94f + travel * 0.01f)),
                radius = w * 0.85f,
            ),
        )

        // Rose accent nestled in the lower right, mostly felt rather than seen.
        drawRect(
            Brush.radialGradient(
                colors = listOf(
                    EchoGlowRose.copy(alpha = 0.07f),
                    Color.Transparent,
                ),
                center = Offset(w * (0.98f + travel * 0.02f), h * 0.82f),
                radius = w * 0.55f,
            ),
        )

        // Vignette keeps the edges deep and focuses the centre.
        drawRect(
            Brush.radialGradient(
                colors = listOf(
                    Color.Transparent,
                    Color(0xFF050508).copy(alpha = 0.58f),
                ),
                center = Offset(w * 0.5f, h * 0.42f),
                radius = w * 1.05f,
            ),
        )

        // A barely-there dusting of light gives the flat gradients some texture.
        val points = listOf(
            0.08f to 0.14f, 0.21f to 0.07f, 0.34f to 0.22f, 0.47f to 0.11f,
            0.63f to 0.18f, 0.76f to 0.06f, 0.89f to 0.27f, 0.95f to 0.12f,
            0.14f to 0.39f, 0.29f to 0.55f, 0.41f to 0.34f, 0.56f to 0.46f,
            0.71f to 0.38f, 0.84f to 0.59f, 0.92f to 0.44f, 0.05f to 0.68f,
            0.19f to 0.83f, 0.31f to 0.72f, 0.66f to 0.76f, 0.79f to 0.9f,
        )
        points.forEachIndexed { index, (x, y) ->
            drawCircle(
                color = Color.White.copy(alpha = if (index % 3 == 0) 0.075f else 0.04f),
                radius = if (index % 4 == 0) 1.1.dp.toPx() else 0.75.dp.toPx(),
                center = Offset(w * x, h * y),
            )
        }
    }
}
