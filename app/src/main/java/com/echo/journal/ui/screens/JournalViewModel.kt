package com.echo.journal.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.echo.journal.data.repository.EchoRepository
import com.echo.journal.domain.JournalEntry
import com.echo.journal.domain.filterJournalEntries
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class JournalUiState(
    val isLoading: Boolean = true,
    val entries: List<JournalEntry> = emptyList(),
    val filteredEntries: List<JournalEntry> = emptyList(),
    val query: String = "",
) {
    val isSearching: Boolean get() = query.isNotBlank()
    val isEmpty: Boolean get() = !isLoading && entries.isEmpty()
    val hasNoResults: Boolean get() = !isLoading && entries.isNotEmpty() && filteredEntries.isEmpty()
}

class JournalViewModel(repository: EchoRepository) : ViewModel() {
    private val query = MutableStateFlow("")

    val uiState: StateFlow<JournalUiState> = combine(
        repository.entries,
        query,
    ) { entries, currentQuery ->
        JournalUiState(
            isLoading = false,
            entries = entries,
            filteredEntries = filterJournalEntries(entries, currentQuery),
            query = currentQuery,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = JournalUiState(),
    )

    fun updateQuery(value: String) {
        query.value = value
    }
}
