package com.echo.journal.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.echo.journal.domain.Mood
import com.echo.journal.domain.greetingFor
import com.echo.journal.domain.todayLongLabel
import com.echo.journal.ui.echoViewModel
import com.echo.journal.ui.mainScreenBottomPadding
import com.echo.journal.ui.rememberRepository
import com.echo.journal.ui.statusBarTopPadding
import com.echo.journal.ui.components.CalloutPill
import com.echo.journal.ui.components.EchoWordmark
import com.echo.journal.ui.components.GlassButton
import com.echo.journal.ui.components.GlassButtonStyle
import com.echo.journal.ui.components.GlassCard
import com.echo.journal.ui.components.GlassLevel
import com.echo.journal.ui.components.GlassSurface
import com.echo.journal.ui.components.GlassTextField
import com.echo.journal.ui.components.JournalEntryCard
import com.echo.journal.ui.components.MoodBadge
import com.echo.journal.ui.components.MoodSelector
import com.echo.journal.ui.components.QuietLabel
import com.echo.journal.ui.components.SectionHeader
import com.echo.journal.ui.theme.EchoAccent
import com.echo.journal.ui.theme.EchoAccentSoft
import com.echo.journal.ui.theme.EchoDanger
import com.echo.journal.ui.theme.EchoRadius
import com.echo.journal.ui.theme.EchoSpacing
import com.echo.journal.ui.theme.EchoSuccess
import com.echo.journal.ui.theme.EchoTextFaint
import com.echo.journal.ui.theme.EchoTextMuted
import com.echo.journal.ui.theme.EchoTextPrimary
import com.echo.journal.ui.theme.moodColor
import java.time.LocalDateTime
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    onWrite: (prompt: String?) -> Unit,
    onOpenEntry: (Long) -> Unit,
    onSeeAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val repository = rememberRepository()
    val viewModel = echoViewModel { HomeViewModel(repository) }
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val now = remember { LocalDateTime.now() }
    val greeting = remember(now) { greetingFor(now.hour) }
    val dateLabel = remember(now) { todayLongLabel(now.toLocalDate()) }

    var selectedMoodKey by rememberSaveable { mutableStateOf<String?>(null) }
    var note by rememberSaveable { mutableStateOf("") }
    var showSaved by remember { mutableStateOf(false) }
    val selectedMood = Mood.fromKey(selectedMoodKey)
    val statusBarTop = statusBarTopPadding()
    val bottomPadding = mainScreenBottomPadding()

    LaunchedEffect(state.snapshot.latestToday?.id) {
        if (selectedMoodKey == null) {
            selectedMoodKey = state.snapshot.latestToday?.mood?.key
        }
    }

    LaunchedEffect(state.checkIn.lastSavedAt) {
        if (state.checkIn.lastSavedAt == null) return@LaunchedEffect
        note = ""
        showSaved = true
        delay(3_000)
        showSaved = false
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .imePadding(),
        contentPadding = PaddingValues(
            start = EchoSpacing.lg,
            end = EchoSpacing.lg,
            top = statusBarTop + 12.dp,
            bottom = bottomPadding,
        ),
    ) {
        item(key = "wordmark") {
            EchoWordmark(
                trailing = {
                    GlassButton(
                        text = "Write",
                        onClick = { onWrite(null) },
                        style = GlassButtonStyle.Secondary,
                        icon = Icons.Rounded.Edit,
                    )
                },
            )
        }

        item(key = "greeting") {
            Spacer(Modifier.height(34.dp))
            Text(
                text = greeting,
                style = MaterialTheme.typography.labelLarge,
                color = EchoAccentSoft,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "How are you\nfeeling today?",
                style = MaterialTheme.typography.displayMedium,
                color = EchoTextPrimary,
            )
            Spacer(Modifier.height(14.dp))
            if (LocalDensity.current.fontScale >= 1.4f) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    QuietLabel(dateLabel)
                    if (state.snapshot.currentStreak > 0) {
                        CalloutPill(
                            text = "${state.snapshot.currentStreak} day streak",
                            icon = Icons.Rounded.LocalFireDepartment,
                            tint = EchoAccentSoft,
                        )
                    }
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    QuietLabel(dateLabel)
                    if (state.snapshot.currentStreak > 0) {
                        CalloutPill(
                            text = "${state.snapshot.currentStreak} day streak",
                            icon = Icons.Rounded.LocalFireDepartment,
                            tint = EchoAccentSoft,
                        )
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = "A quiet place for whatever is here.",
                style = MaterialTheme.typography.bodyMedium,
                color = EchoTextMuted,
            )
            Spacer(Modifier.height(26.dp))
        }

        item(key = "mood-card") {
            GlassSurface(
                modifier = Modifier.fillMaxWidth(),
                level = GlassLevel.Hero,
                contentPadding = PaddingValues(20.dp),
            ) {
                QuietLabel("Today's check-in")
                Spacer(Modifier.height(5.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = when {
                            state.snapshot.checkInsToday == 0 -> "Let it be named."
                            state.snapshot.checkInsToday == 1 -> "One moment noticed today."
                            else -> "${state.snapshot.checkInsToday} moments noticed today."
                        },
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleMedium,
                        color = EchoTextPrimary,
                    )
                    state.snapshot.latestToday?.let { latest ->
                        Spacer(Modifier.width(8.dp))
                        MoodBadge(latest.mood, compact = true)
                    }
                }
                Spacer(Modifier.height(16.dp))
                MoodSelector(
                    selected = selectedMood,
                    onSelect = { mood -> selectedMoodKey = mood.key },
                )
                Spacer(Modifier.height(12.dp))
                GlassTextField(
                    value = note,
                    onValueChange = { note = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = "Add a small note (optional)",
                    maxLines = 3,
                    textStyle = MaterialTheme.typography.bodyMedium,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 13.dp),
                )
                Spacer(Modifier.height(14.dp))
                GlassButton(
                    text = if (state.snapshot.checkInsToday > 0) "Check in again" else "Save check-in",
                    onClick = {
                        selectedMood?.let { viewModel.saveCheckIn(it, note) }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = selectedMood != null && !state.checkIn.isSaving,
                    loading = state.checkIn.isSaving,
                    icon = Icons.Rounded.Check,
                )
                AnimatedVisibility(
                    visible = showSaved,
                    enter = fadeIn() + slideInVertically { it / 3 },
                    exit = fadeOut(),
                ) {
                    Row(
                        modifier = Modifier.padding(top = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(7.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = null,
                            tint = EchoSuccess,
                            modifier = Modifier.size(15.dp),
                        )
                        Text(
                            text = "Check-in saved. Thank you for noticing.",
                            style = MaterialTheme.typography.bodySmall,
                            color = EchoSuccess,
                        )
                    }
                }
                AnimatedVisibility(visible = state.checkIn.error != null) {
                    Text(
                        text = state.checkIn.error.orEmpty(),
                        modifier = Modifier.padding(top = 10.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = EchoDanger,
                    )
                }
                if (state.snapshot.latestToday != null) {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "Last check-in at ${com.echo.journal.domain.timeLabel(state.snapshot.latestToday!!.createdAt)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = EchoTextFaint,
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        item(key = "prompt") {
            GlassSurface(
                modifier = Modifier.fillMaxWidth(),
                level = GlassLevel.Subtle,
                onClick = { onWrite(state.prompt) },
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        EchoAccent.copy(alpha = 0.24f),
                                        EchoAccent.copy(alpha = 0.04f),
                                    ),
                                ),
                            )
                            .border(1.dp, Color.White.copy(alpha = 0.08f), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Spa,
                            contentDescription = null,
                            tint = EchoAccentSoft,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        QuietLabel("A question for today")
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = state.prompt,
                            style = MaterialTheme.typography.titleMedium,
                            color = EchoTextPrimary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = "Write about today's prompt",
                        tint = EchoTextFaint,
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
            Spacer(Modifier.height(28.dp))
        }

        item(key = "recent-header") {
            SectionHeader(
                title = "Recent echoes",
                actionLabel = if (state.recentEntries.isNotEmpty()) "See all" else null,
                onAction = onSeeAll,
            )
            Spacer(Modifier.height(12.dp))
        }

        item(key = "recent-content") {
            when {
                state.isLoading -> {
                    GlassSurface(
                        modifier = Modifier.fillMaxWidth(),
                        level = GlassLevel.Subtle,
                        contentPadding = PaddingValues(24.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = EchoAccent,
                                strokeWidth = 2.dp,
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = "Gathering your echoes\u2026",
                                style = MaterialTheme.typography.bodyMedium,
                                color = EchoTextMuted,
                            )
                        }
                    }
                }

                state.recentEntries.isEmpty() -> {
                    GlassSurface(
                        modifier = Modifier.fillMaxWidth(),
                        level = GlassLevel.Subtle,
                        onClick = { onWrite(null) },
                        contentPadding = PaddingValues(20.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.radialGradient(
                                            listOf(
                                                EchoAccent.copy(alpha = 0.20f),
                                                EchoAccent.copy(alpha = 0.035f),
                                            ),
                                        ),
                                    )
                                    .border(1.dp, Color.White.copy(alpha = 0.07f), CircleShape),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.AutoStories,
                                    contentDescription = null,
                                    tint = EchoAccentSoft,
                                    modifier = Modifier.size(20.dp),
                                )
                            }
                            Spacer(Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "The first page is yours",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = EchoTextPrimary,
                                )
                                Spacer(Modifier.height(3.dp))
                                Text(
                                    text = "Nothing to catch up on. Just begin wherever you are.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = EchoTextMuted,
                                )
                            }
                        }
                    }
                }

                else -> {
                    state.recentEntries.forEachIndexed { index, entry ->
                        JournalEntryCard(
                            entry = entry,
                            onClick = { onOpenEntry(entry.id) },
                        )
                        if (index != state.recentEntries.lastIndex) {
                            Spacer(Modifier.height(10.dp))
                        }
                    }
                }
            }
        }

        item(key = "footer") {
            Spacer(Modifier.height(32.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Lock,
                    contentDescription = null,
                    tint = EchoTextFaint,
                    modifier = Modifier.size(12.dp),
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Everything stays on this device.",
                    style = MaterialTheme.typography.bodySmall,
                    color = EchoTextFaint,
                    fontStyle = FontStyle.Italic,
                )
            }
            Spacer(Modifier.height(14.dp))
        }
    }
}
