package com.echo.journal.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.echo.journal.domain.editorDateLabel
import com.echo.journal.domain.relativeDateLabel
import com.echo.journal.domain.shortDateLabel
import com.echo.journal.domain.wordCountLabel
import com.echo.journal.ui.components.EchoWordmark
import com.echo.journal.ui.components.EmptyState
import com.echo.journal.ui.components.GlassButton
import com.echo.journal.ui.components.GlassButtonStyle
import com.echo.journal.ui.components.GlassDivider
import com.echo.journal.ui.components.GlassIconButton
import com.echo.journal.ui.components.GlassLevel
import com.echo.journal.ui.components.GlassSurface
import com.echo.journal.ui.components.MoodBadge
import com.echo.journal.ui.components.QuietLabel
import com.echo.journal.ui.echoViewModel
import com.echo.journal.ui.navigationBarBottomPadding
import com.echo.journal.ui.rememberRepository
import com.echo.journal.ui.statusBarTopPadding
import com.echo.journal.ui.theme.EchoAccent
import com.echo.journal.ui.theme.EchoDanger
import com.echo.journal.ui.theme.EchoElevated
import com.echo.journal.ui.theme.EchoRadius
import com.echo.journal.ui.theme.EchoSpacing
import com.echo.journal.ui.theme.EchoTextFaint
import com.echo.journal.ui.theme.EchoTextMuted
import com.echo.journal.ui.theme.EchoTextPrimary

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ReaderScreen(
    entryId: Long,
    onBack: () -> Unit,
    onEdit: (Long) -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    val repository = rememberRepository()
    val viewModel = echoViewModel(key = "reader-$entryId") {
        ReaderViewModel(repository, entryId)
    }
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(state.deleted) {
        if (state.deleted) onBack()
    }

    Box(modifier = modifier.fillMaxSize()) {
        val contentTop = statusBarTopPadding() + 82.dp
        val contentBottom = navigationBarBottomPadding() + 36.dp

        when {
            state.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(26.dp),
                        color = EchoAccent,
                        strokeWidth = 2.dp,
                    )
                }
            }

            state.entry == null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = contentTop, start = EchoSpacing.lg, end = EchoSpacing.lg),
                ) {
                    EmptyState(
                        icon = Icons.Rounded.AutoStories,
                        title = "This echo has faded",
                        message = "It may have been deleted from this device.",
                        actionLabel = "Back to journal",
                        onAction = onBack,
                    )
                }
            }

            else -> {
                val entry = state.entry
                if (entry != null) {
                    val sharedModifier = with(sharedTransitionScope) {
                        Modifier.sharedBounds(
                            sharedContentState = rememberSharedContentState(key = "entry-${entry.id}"),
                            animatedVisibilityScope = animatedVisibilityScope,
                        )
                    }

                    LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        start = EchoSpacing.lg,
                        end = EchoSpacing.lg,
                        top = contentTop,
                        bottom = contentBottom,
                    ),
                ) {
                    item(key = "meta") {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            QuietLabel(relativeDateLabel(entry.createdAt))
                            Spacer(Modifier.weight(1f))
                            if (entry.body.isNotBlank()) {
                                Text(
                                    text = wordCountLabel(entry.body),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = EchoTextFaint,
                                )
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                    }

                    item(key = "header") {
                        GlassSurface(
                            modifier = sharedModifier.fillMaxWidth(),
                            level = GlassLevel.Hero,
                            contentPadding = PaddingValues(22.dp),
                        ) {
                            QuietLabel("Echo")
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = entry.displayTitle,
                                style = MaterialTheme.typography.headlineLarge,
                                color = EchoTextPrimary,
                            )
                            if (entry.mood != null) {
                                Spacer(Modifier.height(14.dp))
                                MoodBadge(entry.mood)
                            }
                        }
                        Spacer(Modifier.height(24.dp))
                    }

                    item(key = "delete-error") {
                        AnimatedVisibility(
                            visible = state.deleteError != null,
                            enter = androidx.compose.animation.fadeIn(),
                            exit = androidx.compose.animation.fadeOut(),
                        ) {
                            Column {
                                GlassSurface(
                                    modifier = Modifier.fillMaxWidth(),
                                    level = GlassLevel.Subtle,
                                    onClick = viewModel::clearDeleteError,
                                    contentPadding = PaddingValues(14.dp),
                                ) {
                                    Text(
                                        text = state.deleteError.orEmpty(),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = EchoDanger,
                                    )
                                }
                                Spacer(Modifier.height(16.dp))
                            }
                        }
                    }

                    item(key = "body") {
                        if (entry.body.isBlank()) {
                            Text(
                                text = "No words this time, just a feeling.",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    lineHeight = 29.sp,
                                    fontStyle = FontStyle.Italic,
                                ),
                                color = EchoTextMuted,
                            )
                        } else {
                            SelectionContainer {
                                Text(
                                    text = entry.body,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        lineHeight = 30.sp,
                                        letterSpacing = 0.2.sp,
                                    ),
                                    color = EchoTextPrimary,
                                )
                            }
                        }
                        Spacer(Modifier.height(32.dp))
                        GlassDivider()
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "Written ${editorDateLabel(entry.createdAt)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = EchoTextFaint,
                        )
                        if (entry.updatedAt != entry.createdAt) {
                            Spacer(Modifier.height(3.dp))
                            Text(
                                text = "Edited ${relativeDateLabel(entry.updatedAt)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = EchoTextFaint,
                            )
                        }
                        Spacer(Modifier.height(28.dp))
                    }

                    item(key = "actions") {
                        GlassButton(
                            text = "Edit this echo",
                            onClick = { onEdit(entry.id) },
                            modifier = Modifier.fillMaxWidth(),
                            style = GlassButtonStyle.Secondary,
                            icon = Icons.Rounded.Edit,
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(EchoRadius.pill))
                                .clickable(onClick = { showDeleteDialog = true })
                                .padding(vertical = 14.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.DeleteOutline,
                                contentDescription = null,
                                tint = EchoDanger.copy(alpha = 0.82f),
                                modifier = Modifier.size(16.dp),
                            )
                            Spacer(Modifier.width(7.dp))
                            Text(
                                text = "Delete this echo",
                                style = MaterialTheme.typography.labelLarge,
                                color = EchoDanger.copy(alpha = 0.82f),
                            )
                        }
                        Spacer(Modifier.height(16.dp))
                    }
                }
                }
            }
        }

        // A soft scrim keeps the floating controls legible while the page scrolls beneath them.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(statusBarTopPadding() + 92.dp)
                .background(
                    Brush.verticalGradient(
                        0f to Color(0xE60B0B10),
                        0.7f to Color(0x990B0B10),
                        1f to Color.Transparent,
                    ),
                ),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = EchoSpacing.lg, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            GlassIconButton(
                icon = Icons.Rounded.ArrowBack,
                contentDescription = "Back",
                onClick = onBack,
            )
            Spacer(Modifier.weight(1f))
            if (state.entry != null && !state.isDeleting) {
                GlassIconButton(
                    icon = Icons.Rounded.Edit,
                    contentDescription = "Edit echo",
                    onClick = { onEdit(entryId) },
                )
                Spacer(Modifier.width(8.dp))
                GlassIconButton(
                    icon = Icons.Rounded.DeleteOutline,
                    contentDescription = "Delete echo",
                    onClick = { showDeleteDialog = true },
                )
            }
            if (state.isDeleting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = EchoAccent,
                    strokeWidth = 2.dp,
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = EchoElevated,
            shape = RoundedCornerShape(EchoRadius.card),
            title = {
                Text(
                    text = "Delete this echo?",
                    style = MaterialTheme.typography.titleLarge,
                    color = EchoTextPrimary,
                )
            },
            text = {
                Text(
                    text = "This can't be undone. These words will be removed from this device.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = EchoTextMuted,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteEntry()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = EchoDanger),
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = EchoTextMuted),
                ) {
                    Text("Keep it")
                }
            },
        )
    }
}
