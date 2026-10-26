package com.echo.journal.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.echo.journal.data.repository.EchoRepository
import com.echo.journal.domain.JournalEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ReaderUiState(
    val isLoading: Boolean = true,
    val entry: JournalEntry? = null,
    val isDeleting: Boolean = false,
    val deleteError: String? = null,
    val deleted: Boolean = false,
)

class ReaderViewModel(
    private val repository: EchoRepository,
    entryId: Long,
) : ViewModel() {
    private val _uiState = MutableStateFlow(ReaderUiState())
    val uiState: StateFlow<ReaderUiState> = _uiState

    init {
        viewModelScope.launch {
            repository.observeEntry(entryId).collect { entry ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        entry = entry,
                    )
                }
            }
        }
    }

    fun deleteEntry() {
        val entry = _uiState.value.entry ?: return
        if (_uiState.value.isDeleting) return
        _uiState.update { it.copy(isDeleting = true, deleteError = null) }
        viewModelScope.launch {
            try {
                repository.deleteEntry(entry.id)
                _uiState.update { it.copy(isDeleting = false, deleted = true) }
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(
                        isDeleting = false,
                        deleteError = "Could not delete this echo. Please try again.",
                    )
                }
            }
        }
    }

    fun clearDeleteError() {
        _uiState.update { it.copy(deleteError = null) }
    }
}
