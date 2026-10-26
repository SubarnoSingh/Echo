package com.echo.journal.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.echo.journal.domain.DayMood
import com.echo.journal.domain.InsightsSummary
import com.echo.journal.domain.MoodCheckIn
import com.echo.journal.domain.MoodCount
import com.echo.journal.domain.relativeDateLabel
import com.echo.journal.ui.components.AnimatedProgressRing
import com.echo.journal.ui.components.EchoWordmark
import com.echo.journal.ui.components.EmptyState
import com.echo.journal.ui.components.GlassLevel
import com.echo.journal.ui.components.GlassSurface
import com.echo.journal.ui.components.QuietLabel
import com.echo.journal.ui.components.SectionHeader
import com.echo.journal.ui.echoViewModel
import com.echo.journal.ui.mainScreenBottomPadding
import com.echo.journal.ui.rememberRepository
import com.echo.journal.ui.statusBarTopPadding
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
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun InsightsScreen(
    onGoToCheckIn: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val repository = rememberRepository()
    val viewModel = echoViewModel { InsightsViewModel(repository) }
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val summary = state.summary

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = EchoSpacing.lg,
            end = EchoSpacing.lg,
            top = statusBarTopPadding() + 12.dp,
            bottom = mainScreenBottomPadding(),
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(key = "wordmark") { EchoWordmark() }

        item(key = "title") {
            Spacer(Modifier.height(30.dp))
            Text(
                text = "Your rhythm",
                style = MaterialTheme.typography.displaySmall,
                color = EchoTextPrimary,
            )
            Spacer(Modifier.height(7.dp))
            Text(
                text = "Patterns made only from what you've recorded.",
                style = MaterialTheme.typography.bodyMedium,
                color = EchoTextMuted,
            )
            Spacer(Modifier.height(12.dp))
        }

        when {
            state.isLoading -> {
                item(key = "loading") {
                    GlassSurface(
                        modifier = Modifier.fillMaxWidth(),
                        level = GlassLevel.Subtle,
                        contentPadding = PaddingValues(26.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = EchoAccent,
                                strokeWidth = 2.dp,
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = "Looking for patterns\u2026",
                                style = MaterialTheme.typography.bodyMedium,
                                color = EchoTextMuted,
                            )
                        }
                    }
                }
            }

            summary.totalEntries == 0 && summary.totalCheckIns == 0 -> {
                item(key = "empty") {
                    EmptyState(
                        icon = Icons.Rounded.Spa,
                        title = "A little data goes a long way",
                        message = "Check in with how you feel, or write a first page. Your patterns will gather here quietly \u2014 no judgement, no scores.",
                        actionLabel = "Check in on Home",
                        onAction = onGoToCheckIn,
                    )
                }
            }

            else -> {
                item(key = "stats") {
                    if (LocalDensity.current.fontScale >= 1.4f) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                StatCard(
                                    value = summary.totalEntries.toString(),
                                    label = "Echoes",
                                    icon = Icons.Rounded.AutoStories,
                                    modifier = Modifier.weight(1f),
                                )
                                StatCard(
                                    value = summary.totalCheckIns.toString(),
                                    label = "Check-ins",
                                    icon = Icons.Rounded.CheckCircle,
                                    modifier = Modifier.weight(1f),
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                StatCard(
                                    value = summary.currentStreak.toString(),
                                    label = "Day streak",
                                    icon = Icons.Rounded.LocalFireDepartment,
                                    modifier = Modifier.weight(1f),
                                )
                                Spacer(Modifier.weight(1f))
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            StatCard(
                                value = summary.totalEntries.toString(),
                                label = "Echoes",
                                icon = Icons.Rounded.AutoStories,
                                modifier = Modifier.weight(1f),
                            )
                            StatCard(
                                value = summary.totalCheckIns.toString(),
                                label = "Check-ins",
                                icon = Icons.Rounded.CheckCircle,
                                modifier = Modifier.weight(1f),
                            )
                            StatCard(
                                value = summary.currentStreak.toString(),
                                label = "Day streak",
                                icon = Icons.Rounded.LocalFireDepartment,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }

                if (summary.totalCheckIns == 0) {
                    item(key = "no-moods") {
                        EmptyState(
                            icon = Icons.Rounded.Insights,
                            title = "Your patterns start with a feeling",
                            message = "You've written ${summary.totalEntries} ${if (summary.totalEntries == 1) "echo" else "echoes"}. Add a check-in whenever you're ready to see your moods gather here.",
                            actionLabel = "Check in on Home",
                            onAction = onGoToCheckIn,
                        )
                    }
                } else {
                    item(key = "distribution") {
                        MoodLandscapeCard(summary)
                    }

                    item(key = "week") {
                        WeekCard(summary)
                    }

                    item(key = "recent-check-ins") {
                        RecentCheckInsCard(summary.recentCheckIns)
                    }
                }

                item(key = "disclaimer") {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "Echo reflects only what you choose to record. It is not a clinical assessment or a measure of how you should feel.",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = EchoTextFaint,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    value: String,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
) {
    GlassSurface(
        modifier = modifier,
        level = GlassLevel.Subtle,
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 14.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = EchoAccentSoft,
            modifier = Modifier.size(16.dp),
        )
        Spacer(Modifier.height(10.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            color = EchoTextPrimary,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.5.sp),
            color = EchoTextMuted,
        )
    }
}

@Composable
private fun MoodLandscapeCard(summary: InsightsSummary) {
    val largeText = LocalDensity.current.fontScale >= 1.4f
    GlassSurface(
        modifier = Modifier.fillMaxWidth(),
        level = GlassLevel.Card,
        contentPadding = PaddingValues(20.dp),
    ) {
        if (largeText) {
            Column {
                QuietLabel("Mood landscape")
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "The last 30 days",
                    style = MaterialTheme.typography.titleMedium,
                    color = EchoTextPrimary,
                )
                if (summary.topMood != null) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Most often: ${summary.topMood.label}",
                        style = MaterialTheme.typography.bodySmall,
                        color = EchoTextMuted,
                    )
                }
            }
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    QuietLabel("Mood landscape")
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "The last 30 days",
                        style = MaterialTheme.typography.titleMedium,
                        color = EchoTextPrimary,
                    )
                }
                if (summary.topMood != null) {
                    Text(
                        text = "Most often: ${summary.topMood.label}",
                        style = MaterialTheme.typography.bodySmall,
                        color = EchoTextMuted,
                    )
                }
            }
        }
        Spacer(Modifier.height(18.dp))
        if (summary.recentCheckInCount == 0 || summary.distribution.isEmpty()) {
            Text(
                text = "No check-ins in the last 30 days. Your earlier moods are still in your journal.",
                style = MaterialTheme.typography.bodyMedium,
                color = EchoTextMuted,
            )
        } else if (largeText) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                MoodDonut(
                    distribution = summary.distribution,
                    total = summary.recentCheckInCount,
                    modifier = Modifier.size(128.dp),
                )
                Spacer(Modifier.height(16.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    summary.distribution.forEach { item ->
                        MoodLegendRow(item)
                    }
                }
            }
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                MoodDonut(
                    distribution = summary.distribution,
                    total = summary.recentCheckInCount,
                    modifier = Modifier.size(128.dp),
                )
                Spacer(Modifier.width(20.dp))
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(3.dp),
                ) {
                    summary.distribution.forEach { item ->
                        MoodLegendRow(item)
                    }
                }
            }
        }
    }
}

@Composable
private fun MoodLegendRow(item: MoodCount) {
    val color = moodColor(item.mood.key)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {
                contentDescription = "${item.mood.label}: ${item.count} ${if (item.count == 1) "check-in" else "check-ins"}"
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(color),
        )
        Spacer(Modifier.width(7.dp))
        Text(text = item.mood.emoji, fontSize = 12.sp)
        Spacer(Modifier.width(5.dp))
        Text(
            text = item.mood.label,
            style = MaterialTheme.typography.bodySmall,
            color = EchoTextMuted,
            maxLines = 1,
        )
        Spacer(Modifier.weight(1f))
        Text(
            text = item.count.toString(),
            style = MaterialTheme.typography.labelMedium,
            color = EchoTextPrimary,
        )
    }
}

@Composable
private fun MoodDonut(
    distribution: List<MoodCount>,
    total: Int,
    modifier: Modifier = Modifier,
) {
    val reduceMotion = LocalReduceMotion.current
    val reveal = remember { Animatable(if (reduceMotion) 1f else 0f) }

    LaunchedEffect(distribution, total, reduceMotion) {
        if (reduceMotion) {
            reveal.snapTo(1f)
        } else {
            reveal.snapTo(0f)
            reveal.animateTo(1f, tween(900, easing = FastOutSlowInEasing))
        }
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val stroke = 16.dp.toPx()
            val inset = stroke / 2f
            drawArc(
                color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.055f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = Size(size.width - stroke, size.height - stroke),
                style = Stroke(width = stroke),
            )
            var startAngle = -90f
            distribution.forEach { item ->
                val fullSweep = 360f * item.count.toFloat() / total.toFloat()
                val gap = if (distribution.size > 1) 2.2f else 0f
                val sweep = ((fullSweep - gap).coerceAtLeast(0.8f)) * reveal.value
                drawArc(
                    color = moodColor(item.mood.key),
                    startAngle = startAngle + gap / 2f,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = Offset(inset, inset),
                    size = Size(size.width - stroke, size.height - stroke),
                    style = Stroke(width = stroke, cap = StrokeCap.Butt),
                )
                startAngle += fullSweep
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = total.toString(),
                style = MaterialTheme.typography.headlineSmall,
                color = EchoTextPrimary,
            )
            QuietLabel(if (LocalDensity.current.fontScale >= 1.4f) "Moods" else "Check-ins")
        }
    }
}

@Composable
private fun WeekCard(summary: InsightsSummary) {
    val progress = (summary.checkInDaysLast7 / 7f).coerceIn(0f, 1f)
    GlassSurface(
        modifier = Modifier.fillMaxWidth(),
        level = GlassLevel.Card,
        contentPadding = PaddingValues(20.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AnimatedProgressRing(
                progress = progress,
                modifier = Modifier.size(94.dp),
                strokeWidth = 8.dp,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${summary.checkInDaysLast7}/7",
                        style = MaterialTheme.typography.titleLarge,
                        color = EchoTextPrimary,
                    )
                    QuietLabel("Days")
                }
            }
            Spacer(Modifier.width(20.dp))
            Column(Modifier.weight(1f)) {
                QuietLabel("Your week")
                Spacer(Modifier.height(5.dp))
                Text(
                    text = when {
                        summary.checkInDaysLast7 == 0 -> "A quiet week so far."
                        summary.checkInDaysLast7 == 1 -> "One day held a check-in."
                        else -> "${summary.checkInDaysLast7} days held a check-in."
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = EchoTextPrimary,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = trendCopy(summary),
                    style = MaterialTheme.typography.bodySmall,
                    color = EchoTextMuted,
                )
            }
        }
        Spacer(Modifier.height(18.dp))
        WeeklyMoodStrip(summary.weekMoods)
    }
}

private fun trendCopy(summary: InsightsSummary): String = when {
    summary.checkInsLast7Days == 0 && summary.checkInsPrevious7Days == 0 ->
        "No check-ins this week or last."
    summary.checkInsLast7Days == 0 ->
        "Last week had ${summary.checkInsPrevious7Days} ${if (summary.checkInsPrevious7Days == 1) "check-in" else "check-ins"}."
    summary.checkInsPrevious7Days == 0 ->
        "A new rhythm is taking shape."
    summary.trendDelta > 0 ->
        "${summary.trendDelta} more than the week before."
    summary.trendDelta < 0 ->
        "${-summary.trendDelta} fewer than the week before."
    else -> "The same as the week before."
}

@Composable
private fun WeeklyMoodStrip(days: List<DayMood>) {
    val today = LocalDate.now()
    val moodSlotHeight = 24.dp * LocalDensity.current.fontScale.coerceIn(1f, 2f)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        days.forEach { day ->
            val isToday = day.date == today
            val color = day.mood?.let { moodColor(it.key) } ?: EchoTextFaint
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (isToday) EchoAccent.copy(alpha = 0.10f) else androidx.compose.ui.graphics.Color.White.copy(alpha = 0.03f),
                    )
                    .border(
                        1.dp,
                        if (isToday) EchoAccent.copy(alpha = 0.28f) else androidx.compose.ui.graphics.Color.White.copy(alpha = 0.05f),
                        RoundedCornerShape(14.dp),
                    )
                    .padding(vertical = 8.dp)
                    .semantics(mergeDescendants = true) {
                        val dayName = day.date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
                        contentDescription = if (day.mood != null) {
                            "$dayName: ${day.mood.label}, ${day.checkInCount} ${if (day.checkInCount == 1) "check-in" else "check-ins"}${if (isToday) ", today" else ""}"
                        } else {
                            "$dayName: no check-in${if (isToday) ", today" else ""}"
                        }
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = day.date.dayOfWeek.getDisplayName(TextStyle.NARROW, Locale.getDefault()),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isToday) EchoAccentSoft else EchoTextFaint,
                )
                Spacer(Modifier.height(6.dp))
                Box(
                    modifier = Modifier.height(moodSlotHeight),
                    contentAlignment = Alignment.Center,
                ) {
                    if (day.mood != null) {
                        Text(text = day.mood.emoji, fontSize = 17.sp)
                    } else {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(color.copy(alpha = 0.22f)),
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    text = if (day.checkInCount > 0) day.checkInCount.toString() else "\u00B7",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = EchoTextFaint,
                )
            }
        }
    }
}

@Composable
private fun RecentCheckInsCard(checkIns: List<MoodCheckIn>) {
    if (checkIns.isEmpty()) return
    Column {
        SectionHeader(title = "Recent check-ins")
        Spacer(Modifier.height(2.dp))
        GlassSurface(
            modifier = Modifier.fillMaxWidth(),
            level = GlassLevel.Subtle,
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 6.dp),
        ) {
            checkIns.forEachIndexed { index, checkIn ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(moodColor(checkIn.mood.key).copy(alpha = 0.13f))
                            .border(1.dp, moodColor(checkIn.mood.key).copy(alpha = 0.18f), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(text = checkIn.mood.emoji, fontSize = 16.sp)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = checkIn.mood.label,
                                style = MaterialTheme.typography.titleSmall,
                                color = moodColor(checkIn.mood.key),
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = relativeDateLabel(checkIn.createdAt),
                                style = MaterialTheme.typography.bodySmall,
                                color = EchoTextFaint,
                            )
                        }
                        if (checkIn.note.isNotBlank()) {
                            Spacer(Modifier.height(3.dp))
                            Text(
                                text = checkIn.note,
                                style = MaterialTheme.typography.bodySmall,
                                color = EchoTextMuted,
                            )
                        }
                    }
                }
                if (index != checkIns.lastIndex) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(EchoAccent.copy(alpha = 0.06f)),
                    )
                }
            }
        }
    }
}
