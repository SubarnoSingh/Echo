package com.echo.journal.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.echo.journal.data.repository.EchoRepository
import com.echo.journal.domain.HomeSnapshot
import com.echo.journal.domain.JournalEntry
import com.echo.journal.domain.Mood
import com.echo.journal.domain.buildHomeSnapshot
import com.echo.journal.domain.dailyPrompt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CheckInUiState(
    val isSaving: Boolean = false,
    val lastSavedAt: Long? = null,
    val savedMood: Mood? = null,
    val error: String? = null,
)

data class HomeUiState(
    val isLoading: Boolean = true,
    val recentEntries: List<JournalEntry> = emptyList(),
    val snapshot: HomeSnapshot = HomeSnapshot(),
    val prompt: String = "",
    val checkIn: CheckInUiState = CheckInUiState(),
)

class HomeViewModel(private val repository: EchoRepository) : ViewModel() {
    private val checkInState = MutableStateFlow(CheckInUiState())

    val uiState: StateFlow<HomeUiState> = combine(
        repository.entries,
        repository.checkIns,
        checkInState,
    ) { entries, checkIns, checkIn ->
        val now = System.currentTimeMillis()
        HomeUiState(
            isLoading = false,
            recentEntries = entries.take(3),
            snapshot = buildHomeSnapshot(entries, checkIns, now),
            prompt = dailyPrompt(),
            checkIn = checkIn,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState(),
    )

    fun saveCheckIn(mood: Mood, note: String) {
        if (checkInState.value.isSaving) return
        checkInState.update { it.copy(isSaving = true, error = null) }
        viewModelScope.launch {
            try {
                repository.addCheckIn(mood, note)
                checkInState.update {
                    it.copy(
                        isSaving = false,
                        lastSavedAt = System.currentTimeMillis(),
                        savedMood = mood,
                        error = null,
                    )
                }
            } catch (error: Exception) {
                checkInState.update {
                    it.copy(
                        isSaving = false,
                        error = "Could not save your check-in. Please try again.",
                    )
                }
            }
        }
    }

    fun clearError() {
        checkInState.update { it.copy(error = null) }
    }
}
