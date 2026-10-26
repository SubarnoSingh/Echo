package com.echo.journal.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.echo.journal.data.repository.EchoRepository
import com.echo.journal.domain.InsightsSummary
import com.echo.journal.domain.buildInsights
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class InsightsUiState(
    val isLoading: Boolean = true,
    val summary: InsightsSummary = InsightsSummary(),
)

class InsightsViewModel(repository: EchoRepository) : ViewModel() {
    val uiState: StateFlow<InsightsUiState> = combine(
        repository.entries,
        repository.checkIns,
    ) { entries, checkIns ->
        InsightsUiState(
            isLoading = false,
            summary = buildInsights(entries, checkIns),
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = InsightsUiState(),
    )
}
