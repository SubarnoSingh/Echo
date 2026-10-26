package com.echo.journal.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.echo.journal.ui.components.EchoWordmark
import com.echo.journal.ui.components.EmptyState
import com.echo.journal.ui.components.GlassButton
import com.echo.journal.ui.components.GlassButtonStyle
import com.echo.journal.ui.components.GlassLevel
import com.echo.journal.ui.components.GlassSurface
import com.echo.journal.ui.components.JournalEntryCard
import com.echo.journal.ui.components.QuietLabel
import com.echo.journal.ui.echoViewModel
import com.echo.journal.ui.mainScreenBottomPadding
import com.echo.journal.ui.rememberRepository
import com.echo.journal.ui.statusBarTopPadding
import com.echo.journal.ui.theme.EchoAccent
import com.echo.journal.ui.theme.EchoAccentSoft
import com.echo.journal.ui.theme.EchoRadius
import com.echo.journal.ui.theme.EchoSpacing
import com.echo.journal.ui.theme.EchoTextFaint
import com.echo.journal.ui.theme.EchoTextMuted
import com.echo.journal.ui.theme.EchoTextPrimary

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun JournalScreen(
    onWrite: () -> Unit,
    onOpenEntry: (Long) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    val repository = rememberRepository()
    val viewModel = echoViewModel { JournalViewModel(repository) }
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .imePadding(),
        contentPadding = PaddingValues(
            start = EchoSpacing.lg,
            end = EchoSpacing.lg,
            top = statusBarTopPadding() + 12.dp,
            bottom = mainScreenBottomPadding(),
        ),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item(key = "wordmark") {
            EchoWordmark(
                trailing = {
                    GlassButton(
                        text = "Write",
                        onClick = onWrite,
                        style = GlassButtonStyle.Secondary,
                        icon = Icons.Rounded.Edit,
                    )
                },
            )
        }

        item(key = "title") {
            Spacer(Modifier.height(30.dp))
            Text(
                text = "Your journal",
                style = MaterialTheme.typography.displaySmall,
                color = EchoTextPrimary,
            )
            Spacer(Modifier.height(7.dp))
            Text(
                text = "Every page you've kept, held quietly in one place.",
                style = MaterialTheme.typography.bodyMedium,
                color = EchoTextMuted,
            )
            Spacer(Modifier.height(20.dp))
            JournalSearchField(
                query = state.query,
                onQueryChange = viewModel::updateQuery,
                focusManager = focusManager,
            )
            Spacer(Modifier.height(8.dp))
        }

        if (!state.isLoading) {
            item(key = "count") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    QuietLabel(
                        if (state.isSearching) {
                            "${state.filteredEntries.size} ${if (state.filteredEntries.size == 1) "result" else "results"}"
                        } else {
                            "${state.entries.size} ${if (state.entries.size == 1) "echo" else "echoes"}"
                        },
                    )
                    Spacer(Modifier.weight(1f))
                    if (!state.isSearching && state.entries.isNotEmpty()) {
                        Text(
                            text = "Newest first",
                            style = MaterialTheme.typography.bodySmall,
                            color = EchoTextFaint,
                        )
                    }
                }
            }
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
                                text = "Opening your journal\u2026",
                                style = MaterialTheme.typography.bodyMedium,
                                color = EchoTextMuted,
                            )
                        }
                    }
                }
            }

            state.isEmpty -> {
                item(key = "empty") {
                    EmptyState(
                        icon = Icons.Rounded.AutoStories,
                        title = "Your first echo is waiting",
                        message = "There's no right way to start. A sentence, a feeling, a single line is enough.",
                        actionLabel = "Write an entry",
                        onAction = onWrite,
                    )
                }
            }

            state.hasNoResults -> {
                item(key = "no-results") {
                    EmptyState(
                        icon = Icons.Rounded.Search,
                        title = "No echoes found",
                        message = "Nothing here matches \u201C${state.query.trim()}\u201D. Try a feeling, a word, or a date.",
                        actionLabel = "Clear search",
                        onAction = { viewModel.updateQuery("") },
                    )
                }
            }

            else -> {
                items(
                    items = state.filteredEntries,
                    key = { it.id },
                    contentType = { "journal-entry" },
                ) { entry ->
                    val sharedModifier = with(sharedTransitionScope) {
                        Modifier.sharedBounds(
                            sharedContentState = rememberSharedContentState(key = "entry-${entry.id}"),
                            animatedVisibilityScope = animatedVisibilityScope,
                        )
                    }
                    JournalEntryCard(
                        entry = entry,
                        onClick = { onOpenEntry(entry.id) },
                        modifier = sharedModifier.animateItem(
                            fadeInSpec = tween(260),
                            fadeOutSpec = tween(180),
                        ),
                    )
                    Spacer(Modifier.height(2.dp))
                }
            }
        }
    }
}

@Composable
private fun JournalSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    focusManager: FocusManager,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(EchoRadius.pill))
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, Color.White.copy(alpha = 0.09f), RoundedCornerShape(EchoRadius.pill))
            .padding(start = 16.dp, end = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = Icons.Rounded.Search,
            contentDescription = null,
            tint = EchoTextMuted,
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.width(10.dp))
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(color = EchoTextPrimary),
            cursorBrush = Brush.verticalGradient(listOf(EchoAccentSoft, EchoAccent)),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
            decorationBox = { innerTextField ->
                Box(contentAlignment = Alignment.CenterStart) {
                    if (query.isEmpty()) {
                        Text(
                            text = "Search your echoes",
                            style = MaterialTheme.typography.bodyMedium,
                            color = EchoTextMuted.copy(alpha = 0.75f),
                        )
                    }
                    innerTextField()
                }
            },
        )
        AnimatedVisibility(
            visible = query.isNotEmpty(),
            enter = fadeIn(tween(140)),
            exit = fadeOut(tween(120)),
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(EchoRadius.pill))
                    .clickable(
                        onClickLabel = "Clear search",
                        onClick = { onQueryChange("") },
                    )
                    .semantics { contentDescription = "Clear search" },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = null,
                    tint = EchoTextMuted,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}
