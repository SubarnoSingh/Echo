package com.echo.journal.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddReaction
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.echo.journal.domain.Mood
import com.echo.journal.domain.editorDateLabel
import com.echo.journal.domain.shortDateLabel
import com.echo.journal.domain.timeLabel
import com.echo.journal.domain.wordCountLabel
import com.echo.journal.ui.components.EchoWordmark
import com.echo.journal.ui.components.EmptyState
import com.echo.journal.ui.components.GlassButton
import com.echo.journal.ui.components.GlassButtonStyle
import com.echo.journal.ui.components.GlassIconButton
import com.echo.journal.ui.components.GlassLevel
import com.echo.journal.ui.components.GlassSurface
import com.echo.journal.ui.components.MoodSelector
import com.echo.journal.ui.components.QuietLabel
import com.echo.journal.ui.echoViewModel
import com.echo.journal.ui.rememberRepository
import com.echo.journal.ui.statusBarTopPadding
import com.echo.journal.ui.theme.EchoAccent
import com.echo.journal.ui.theme.EchoAccentSoft
import com.echo.journal.ui.theme.EchoDanger
import com.echo.journal.ui.theme.EchoElevated
import com.echo.journal.ui.theme.EchoNight
import com.echo.journal.ui.theme.EchoRadius
import com.echo.journal.ui.theme.EchoSpacing
import com.echo.journal.ui.theme.EchoTextFaint
import com.echo.journal.ui.theme.EchoTextMuted
import com.echo.journal.ui.theme.EchoTextPrimary
import com.echo.journal.ui.theme.moodColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    entryId: Long?,
    prompt: String,
    onBack: () -> Unit,
    onSaved: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val repository = rememberRepository()
    val normalizedEntryId = entryId?.takeIf { it > 0 }
    val viewModel = echoViewModel(key = "editor-${normalizedEntryId ?: "new"}") {
        EditorViewModel(repository, normalizedEntryId)
    }
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val focusManager = LocalFocusManager.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val startedAt = remember { System.currentTimeMillis() }

    var title by rememberSaveable(normalizedEntryId) { mutableStateOf("") }
    var body by rememberSaveable(normalizedEntryId) { mutableStateOf("") }
    var moodKey by rememberSaveable(normalizedEntryId) { mutableStateOf<String?>(null) }
    var baselineTitle by rememberSaveable(normalizedEntryId) { mutableStateOf("") }
    var baselineBody by rememberSaveable(normalizedEntryId) { mutableStateOf("") }
    var baselineMoodKey by rememberSaveable(normalizedEntryId) { mutableStateOf<String?>(null) }
    var initialized by rememberSaveable(normalizedEntryId) { mutableStateOf(normalizedEntryId == null) }
    var showDiscardDialog by rememberSaveable { mutableStateOf(false) }
    var showMoodSheet by remember { mutableStateOf(false) }
    var handledSave by rememberSaveable { mutableStateOf(false) }

    val selectedMood = Mood.fromKey(moodKey)
    val hasContent = title.isNotBlank() || body.isNotBlank()
    val dirty = initialized && (
        title != baselineTitle ||
            body != baselineBody ||
            moodKey != baselineMoodKey
        )
    val canSave = initialized && hasContent && !state.isSaving && !state.notFound && state.savedId == null

    LaunchedEffect(state.entry, state.isLoading) {
        if (!initialized && !state.isLoading) {
            val entry = state.entry
            title = entry?.title.orEmpty()
            body = entry?.body.orEmpty()
            moodKey = entry?.mood?.key
            baselineTitle = title
            baselineBody = body
            baselineMoodKey = moodKey
            initialized = true
        }
    }

    LaunchedEffect(state.savedId) {
        if (state.savedId != null) {
            if (!handledSave) {
                handledSave = true
                delay(520)
            }
            onSaved()
        }
    }

    val requestBack: () -> Unit = {
        if (state.isSaving) {
            // Let the save finish rather than risking a half-written record.
        } else if (dirty && state.savedId == null) {
            showDiscardDialog = true
        } else {
            onBack()
        }
    }

    BackHandler {
        if (showMoodSheet) {
            showMoodSheet = false
        } else {
            requestBack()
        }
    }

    val requestSave: () -> Unit = {
        if (canSave) {
            focusManager.clearFocus()
            viewModel.save(title, body, selectedMood)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .padding(horizontal = EchoSpacing.lg),
    ) {
        EditorTopBar(
            isEditing = normalizedEntryId != null,
            isSaving = state.isSaving,
            isSaved = state.savedId != null,
            canSave = canSave,
            onBack = requestBack,
            onSave = requestSave,
        )

        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = EchoAccent,
                        strokeWidth = 2.dp,
                    )
                }
            }

            state.notFound -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center,
                ) {
                    EmptyState(
                        icon = Icons.Rounded.ArrowBack,
                        title = "This echo is no longer here",
                        message = "It may have been deleted on another screen.",
                        actionLabel = "Go back",
                        onAction = onBack,
                    )
                }
            }

            else -> {
                AnimatedVisibility(visible = state.error != null) {
                    Text(
                        text = state.error.orEmpty(),
                        modifier = Modifier.padding(top = 6.dp, bottom = 6.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = EchoDanger,
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    QuietLabel(
                        if (normalizedEntryId == null) "New echo" else "Edit echo",
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = if (normalizedEntryId == null) {
                            "${shortDateLabel(startedAt)} at ${timeLabel(startedAt)}"
                        } else {
                            shortDateLabel(state.entry?.createdAt ?: startedAt)
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = EchoTextFaint,
                    )
                }
                Spacer(Modifier.height(10.dp))

                if (prompt.isNotBlank() && normalizedEntryId == null) {
                    GlassSurface(
                        modifier = Modifier.fillMaxWidth(),
                        level = GlassLevel.Subtle,
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Rounded.Spa,
                                contentDescription = null,
                                tint = EchoAccentSoft,
                                modifier = Modifier.size(16.dp),
                            )
                            Spacer(Modifier.width(10.dp))
                            Column {
                                QuietLabel("A question to sit with")
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = prompt,
                                    style = MaterialTheme.typography.bodySmall.copy(fontStyle = FontStyle.Italic),
                                    color = EchoTextMuted,
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                }

                BasicTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { contentDescription = "Journal title" },
                    textStyle = MaterialTheme.typography.headlineMedium.copy(
                        color = EchoTextPrimary,
                    ),
                    cursorBrush = Brush.verticalGradient(listOf(EchoAccentSoft, EchoAccent)),
                    maxLines = 2,
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Next,
                    ),
                    decorationBox = { innerTextField ->
                        Box {
                            if (title.isEmpty()) {
                                Text(
                                    text = "A title, if one comes",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = EchoTextMuted.copy(alpha = 0.5f),
                                )
                            }
                            innerTextField()
                        }
                    },
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    MoodSelectorPill(
                        mood = selectedMood,
                        onClick = { showMoodSheet = true },
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        text = when {
                            body.isBlank() && title.isBlank() -> "A page only you will read"
                            body.isBlank() -> "A feeling only"
                            else -> wordCountLabel(body)
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = EchoTextFaint,
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                ) {
                    BasicTextField(
                        value = body,
                        onValueChange = { body = it },
                        modifier = Modifier
                            .fillMaxSize()
                            .semantics { contentDescription = "Journal entry" },
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = EchoTextPrimary,
                            lineHeight = 29.sp,
                        ),
                        cursorBrush = Brush.verticalGradient(listOf(EchoAccentSoft, EchoAccent)),
                        maxLines = Int.MAX_VALUE,
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Default,
                        ),
                        decorationBox = { innerTextField ->
                            Box {
                                if (body.isEmpty()) {
                                    Text(
                                        text = "Begin anywhere.\nNo one else will read this.",
                                        style = MaterialTheme.typography.bodyLarge.copy(
                                            lineHeight = 29.sp,
                                        ),
                                        color = EchoTextMuted.copy(alpha = 0.55f),
                                    )
                                }
                                innerTextField()
                            }
                        },
                    )
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }

    if (showMoodSheet) {
        ModalBottomSheet(
            onDismissRequest = { showMoodSheet = false },
            sheetState = sheetState,
            containerColor = Color.Transparent,
            contentColor = EchoTextPrimary,
            scrimColor = Color.Black.copy(alpha = 0.55f),
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(top = 10.dp, bottom = 6.dp)
                        .width(36.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(EchoRadius.pill))
                        .background(Color.White.copy(alpha = 0.18f)),
                )
            },
        ) {
            GlassSurface(
                modifier = Modifier.fillMaxWidth(),
                level = GlassLevel.Hero,
                shape = RoundedCornerShape(topStart = EchoRadius.panel, topEnd = EchoRadius.panel),
                contentPadding = PaddingValues(
                    start = 22.dp,
                    end = 22.dp,
                    top = 12.dp,
                    bottom = navigationBottomInset() + 20.dp,
                ),
            ) {
                Text(
                    text = "How does this feel?",
                    style = MaterialTheme.typography.titleLarge,
                    color = EchoTextPrimary,
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = "Optional. A feeling can be a whole entry on its own.",
                    style = MaterialTheme.typography.bodySmall,
                    color = EchoTextMuted,
                )
                Spacer(Modifier.height(18.dp))
                MoodSelector(
                    selected = selectedMood,
                    onSelect = { moodKey = it.key },
                )
                Spacer(Modifier.height(18.dp))
                GlassButton(
                    text = "Use this mood",
                    onClick = {
                        scope.launch {
                            sheetState.hide()
                            showMoodSheet = false
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.Rounded.Check,
                )
                if (selectedMood != null) {
                    TextButton(
                        onClick = {
                            moodKey = null
                            scope.launch {
                                sheetState.hide()
                                showMoodSheet = false
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.textButtonColors(contentColor = EchoTextMuted),
                    ) {
                        Text("Remove mood")
                    }
                }
            }
        }
    }

    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            containerColor = EchoElevated,
            shape = RoundedCornerShape(EchoRadius.card),
            title = {
                Text(
                    text = if (normalizedEntryId == null) "Discard this echo?" else "Discard your changes?",
                    style = MaterialTheme.typography.titleLarge,
                    color = EchoTextPrimary,
                )
            },
            text = {
                Text(
                    text = if (normalizedEntryId == null) {
                        "Your unsaved words will be lost."
                    } else {
                        "This echo will return to its last saved version."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = EchoTextMuted,
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDiscardDialog = false
                        onBack()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = EchoDanger),
                ) {
                    Text("Discard")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDiscardDialog = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = EchoTextMuted),
                ) {
                    Text("Keep writing")
                }
            },
        )
    }
}

@Composable
private fun EditorTopBar(
    isEditing: Boolean,
    isSaving: Boolean,
    isSaved: Boolean,
    canSave: Boolean,
    onBack: () -> Unit,
    onSave: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        GlassIconButton(
            icon = Icons.Rounded.ArrowBack,
            contentDescription = "Close editor",
            onClick = onBack,
            enabled = !isSaving,
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = if (isEditing) "Editing echo" else "New echo",
                style = MaterialTheme.typography.titleSmall,
                color = EchoTextPrimary,
            )
            Text(
                text = when {
                    isSaving -> "Saving\u2026"
                    isSaved -> "Saved on this device"
                    else -> "Private by default"
                },
                style = MaterialTheme.typography.bodySmall,
                color = if (isSaved) EchoAccentSoft else EchoTextFaint,
            )
        }
        GlassButton(
            text = if (isSaved) "Saved" else "Save",
            onClick = onSave,
            enabled = canSave,
            loading = isSaving,
            icon = if (isSaved) Icons.Rounded.Check else null,
        )
    }
}

@Composable
private fun MoodSelectorPill(
    mood: Mood?,
    onClick: () -> Unit,
) {
    val color = mood?.let { moodColor(it.key) } ?: EchoAccentSoft
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(EchoRadius.pill))
            .background(color.copy(alpha = 0.10f))
            .border(1.dp, color.copy(alpha = 0.18f), RoundedCornerShape(EchoRadius.pill))
            .clickable(
                onClickLabel = if (mood == null) "Add a mood" else "Change mood",
                onClick = onClick,
            )
            .semantics {
                contentDescription = if (mood == null) {
                    "No mood selected. Add a mood."
                } else {
                    "Mood: ${mood.label}. Change mood."
                }
            }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        if (mood == null) {
            Icon(
                imageVector = Icons.Rounded.AddReaction,
                contentDescription = null,
                tint = EchoAccentSoft,
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = "Add a mood",
                style = MaterialTheme.typography.labelLarge,
                color = EchoAccentSoft,
            )
        } else {
            Text(text = mood.emoji, fontSize = 13.sp)
            Text(
                text = mood.label,
                style = MaterialTheme.typography.labelLarge,
                color = color,
            )
        }
        Icon(
            imageVector = Icons.Rounded.ExpandMore,
            contentDescription = null,
            tint = EchoTextMuted,
            modifier = Modifier.size(14.dp),
        )
    }
}

@Composable
private fun navigationBottomInset() = WindowInsets.navigationBars
    .asPaddingValues()
    .calculateBottomPadding()
