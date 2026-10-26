package com.echo.journal.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.echo.journal.data.repository.EchoRepository
import com.echo.journal.domain.JournalEntry
import com.echo.journal.domain.Mood
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EditorUiState(
    val isLoading: Boolean = true,
    val entry: JournalEntry? = null,
    val isSaving: Boolean = false,
    val savedId: Long? = null,
    val error: String? = null,
    val notFound: Boolean = false,
)

class EditorViewModel(
    private val repository: EchoRepository,
    private val entryId: Long?,
) : ViewModel() {
    private val _uiState = MutableStateFlow(
        EditorUiState(isLoading = entryId != null),
    )
    val uiState: StateFlow<EditorUiState> = _uiState

    init {
        if (entryId != null) {
            viewModelScope.launch {
                val entry = runCatching { repository.findEntry(entryId) }.getOrNull()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        entry = entry,
                        notFound = entry == null,
                    )
                }
            }
        }
    }

    fun save(title: String, body: String, mood: Mood?) {
        if (_uiState.value.isSaving) return
        _uiState.update { it.copy(isSaving = true, error = null) }
        viewModelScope.launch {
            try {
                val savedId = repository.saveEntry(
                    id = entryId,
                    title = title,
                    body = body,
                    mood = mood,
                )
                _uiState.update { it.copy(isSaving = false, savedId = savedId) }
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        error = "Could not save this echo. Your words are still here \u2014 try again.",
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
