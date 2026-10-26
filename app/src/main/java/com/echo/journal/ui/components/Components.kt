package com.echo.journal.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowForward
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.echo.journal.domain.JournalEntry
import com.echo.journal.domain.Mood
import com.echo.journal.domain.relativeDateLabel
import com.echo.journal.ui.theme.EchoAccent
import com.echo.journal.ui.theme.EchoAccentSoft
import com.echo.journal.ui.theme.EchoGlowViolet
import com.echo.journal.ui.theme.EchoRadius
import com.echo.journal.ui.theme.EchoSpacing
import com.echo.journal.ui.theme.EchoTextFaint
import com.echo.journal.ui.theme.EchoTextMuted
import com.echo.journal.ui.theme.EchoTextPrimary
import com.echo.journal.ui.theme.LocalReduceMotion
import com.echo.journal.ui.theme.moodColor

@Composable
fun EchoMark(
    modifier: Modifier = Modifier,
    size: Dp = 34.dp,
) {
    Canvas(modifier = modifier.size(size)) {
        drawCircle(Color(0xFF161222))
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(EchoGlowViolet.copy(alpha = 0.5f), Color.Transparent),
                center = Offset(this.size.width * 0.35f, this.size.height * 0.25f),
                radius = this.size.width * 0.7f,
            ),
        )
        val stroke = this.size.width * 0.055f
        drawArc(
            color = EchoAccent.copy(alpha = 0.9f),
            startAngle = 200f,
            sweepAngle = 140f,
            useCenter = false,
            topLeft = Offset(this.size.width * 0.22f, this.size.height * 0.24f),
            size = Size(this.size.width * 0.56f, this.size.height * 0.56f),
            style = Stroke(width = stroke, cap = StrokeCap.Round),
        )
        drawArc(
            color = EchoAccentSoft.copy(alpha = 0.5f),
            startAngle = 200f,
            sweepAngle = 140f,
            useCenter = false,
            topLeft = Offset(this.size.width * 0.11f, this.size.height * 0.13f),
            size = Size(this.size.width * 0.78f, this.size.height * 0.78f),
            style = Stroke(width = stroke * 0.8f, cap = StrokeCap.Round),
        )
        drawCircle(
            color = EchoAccentSoft,
            radius = this.size.width * 0.075f,
            center = Offset(this.size.width * 0.5f, this.size.height * 0.61f),
        )
    }
}

@Composable
fun EchoWordmark(
    modifier: Modifier = Modifier,
    trailing: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        EchoMark()
        Spacer(Modifier.width(10.dp))
        Text(
            text = "ECHO",
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 3.4.sp),
            color = EchoTextMuted,
        )
        Spacer(Modifier.weight(1f))
        trailing?.invoke()
    }
}

@Composable
fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = EchoTextPrimary,
        )
        Spacer(Modifier.weight(1f))
        if (actionLabel != null && onAction != null) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(EchoRadius.pill))
                    .clickable(
                        role = Role.Button,
                        onClick = onAction,
                    )
                    .defaultMinSize(minHeight = 48.dp)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = actionLabel,
                    style = MaterialTheme.typography.labelLarge,
                    color = EchoAccentSoft,
                )
                Icon(
                    imageVector = Icons.Rounded.ArrowForward,
                    contentDescription = null,
                    tint = EchoAccentSoft,
                    modifier = Modifier.size(14.dp),
                )
            }
        }
    }
}

@Composable
fun MoodBadge(
    mood: Mood,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    val color = moodColor(mood.key)
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(EchoRadius.pill))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.20f), RoundedCornerShape(EchoRadius.pill))
            .padding(
                horizontal = if (compact) 8.dp else 10.dp,
                vertical = if (compact) 3.dp else 5.dp,
            )
            .semantics(mergeDescendants = true) {
                contentDescription = "${mood.label} mood"
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        Text(text = mood.emoji, fontSize = if (compact) 11.sp else 12.sp)
        Text(
            text = mood.label,
            style = MaterialTheme.typography.labelMedium,
            color = color,
            maxLines = 1,
        )
    }
}

@Composable
fun MoodSelector(
    selected: Mood?,
    onSelect: (Mood) -> Unit,
    modifier: Modifier = Modifier,
) {
    val haptics = LocalHapticFeedback.current
    val fontScale = LocalDensity.current.fontScale
    val columns = when {
        fontScale >= 1.4f -> 2
        fontScale >= 1.15f -> 3
        else -> 4
    }
    val rows = Mood.entries.chunked(columns)
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        rows.forEach { rowMoods ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                rowMoods.forEach { mood ->
                    MoodOption(
                        mood = mood,
                        selected = selected == mood,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            onSelect(mood)
                        },
                    )
                }
                repeat(columns - rowMoods.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun MoodOption(
    mood: Mood,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = moodColor(mood.key)
    val scale by animateFloatAsState(
        targetValue = if (selected) 1f else 0.96f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMediumLow),
        label = "mood-scale",
    )
    val borderColor by androidx.compose.animation.animateColorAsState(
        targetValue = if (selected) color.copy(alpha = 0.65f) else Color.White.copy(alpha = 0.06f),
        label = "mood-border",
    )
    val fillColor by androidx.compose.animation.animateColorAsState(
        targetValue = if (selected) color.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.035f),
        label = "mood-fill",
    )
    Box(
        modifier = modifier
            .heightIn(min = 68.dp)
            .scale(scale)
            .clip(RoundedCornerShape(EchoRadius.md))
            .background(fillColor)
            .border(1.dp, borderColor, RoundedCornerShape(EchoRadius.md))
            .selectable(
                selected = selected,
                role = Role.RadioButton,
                onClick = onClick,
            )
            .semantics {
                this.selected = selected
                contentDescription = "${mood.label} mood"
                stateDescription = if (selected) "Selected" else "Not selected"
            },
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = mood.emoji, fontSize = 22.sp)
            Spacer(Modifier.height(3.dp))
            Text(
                text = mood.label,
                style = MaterialTheme.typography.labelMedium,
                color = if (selected) color else EchoTextMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        if (selected) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = null,
                tint = color,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(6.dp)
                    .size(12.dp),
            )
        }
    }
}

@Composable
fun AnimatedProgressRing(
    progress: Float,
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 10.dp,
    trackColor: Color = Color.White.copy(alpha = 0.07f),
    brush: Brush = Brush.sweepGradient(listOf(EchoAccentSoft, EchoAccent, EchoGlowViolet)),
    content: @Composable () -> Unit = {},
) {
    val reduceMotion = LocalReduceMotion.current
    val clamped = progress.coerceIn(0f, 1f)
    val animated by animateFloatAsState(
        targetValue = clamped,
        animationSpec = if (reduceMotion) {
            androidx.compose.animation.core.snap()
        } else {
            tween(durationMillis = 1100, easing = androidx.compose.animation.core.FastOutSlowInEasing)
        },
        label = "ring-progress",
    )

    Box(
        modifier = modifier.aspectRatio(1f),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val strokePx = strokeWidth.toPx()
            val inset = strokePx / 2f
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = Size(size.width - strokePx, size.height - strokePx),
                style = Stroke(width = strokePx, cap = StrokeCap.Round),
            )
            if (animated > 0f) {
                drawArc(
                    brush = brush,
                    startAngle = -90f,
                    sweepAngle = 360f * animated,
                    useCenter = false,
                    topLeft = Offset(inset, inset),
                    size = Size(size.width - strokePx, size.height - strokePx),
                    style = Stroke(width = strokePx, cap = StrokeCap.Round),
                )
            }
        }
        content()
    }
}

@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    GlassSurface(
        modifier = modifier.fillMaxWidth(),
        level = GlassLevel.Subtle,
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 36.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(EchoAccent.copy(alpha = 0.22f), EchoAccent.copy(alpha = 0.04f)),
                        ),
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.09f), CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = EchoAccentSoft,
                    modifier = Modifier.size(24.dp),
                )
            }
            Spacer(Modifier.height(18.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = EchoTextPrimary,
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = EchoTextMuted,
                textAlign = TextAlign.Center,
            )
            if (actionLabel != null && onAction != null) {
                Spacer(Modifier.height(22.dp))
                GlassButton(
                    text = actionLabel,
                    onClick = onAction,
                    style = GlassButtonStyle.Secondary,
                    icon = Icons.Rounded.Add,
                )
            }
        }
    }
}

@Composable
fun JournalEntryCard(
    entry: JournalEntry,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showArrow: Boolean = true,
) {
    GlassSurface(
        modifier = modifier.fillMaxWidth(),
        level = GlassLevel.Card,
        onClick = onClick,
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp),
    ) {
        val largeText = LocalDensity.current.fontScale >= 1.4f
        if (largeText) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = relativeDateLabel(entry.createdAt).uppercase(),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.1.sp),
                    color = EchoTextFaint,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (showArrow) {
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Rounded.ArrowForward,
                        contentDescription = null,
                        tint = EchoTextFaint,
                        modifier = Modifier.size(14.dp),
                    )
                }
            }
            entry.mood?.let { mood ->
                Spacer(Modifier.height(8.dp))
                MoodBadge(mood, compact = true)
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = relativeDateLabel(entry.createdAt).uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.1.sp),
                    color = EchoTextFaint,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.weight(1f))
                entry.mood?.let { MoodBadge(it, compact = true) }
                if (showArrow) {
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Rounded.ArrowForward,
                        contentDescription = null,
                        tint = EchoTextFaint,
                        modifier = Modifier.size(14.dp),
                    )
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(
            text = entry.displayTitle,
            style = MaterialTheme.typography.titleLarge,
            color = EchoTextPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
        if (entry.preview.isNotBlank()) {
            Spacer(Modifier.height(6.dp))
            Text(
                text = entry.preview,
                style = MaterialTheme.typography.bodyMedium,
                color = EchoTextMuted,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun NewEntryButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Write something",
) {
    GlassButton(
        text = label,
        onClick = onClick,
        modifier = modifier,
        icon = Icons.Rounded.AutoStories,
    )
}

@Composable
fun MetaDot() {
    Box(
        modifier = Modifier
            .size(3.dp)
            .clip(CircleShape)
            .background(EchoTextFaint),
    )
}

@Composable
fun QuietLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        modifier = modifier,
        style = MaterialTheme.typography.labelSmall.copy(
            letterSpacing = 1.5.sp,
            fontWeight = FontWeight.Medium,
        ),
        color = EchoTextFaint,
    )
}

@Composable
fun VerticalFade(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .drawBehind {
                drawRect(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color(0x14000000), Color.Transparent),
                    ),
                )
            }
            .defaultMinSize(minHeight = 1.dp)
            .padding(horizontal = 0.dp),
    ) {
        content()
    }
}
