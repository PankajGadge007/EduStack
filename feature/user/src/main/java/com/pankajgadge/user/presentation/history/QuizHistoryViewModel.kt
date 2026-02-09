package com.pankajgadge.user.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.pankajgadge.core.common.result.Result
import com.pankajgadge.core.domain.model.QuizResult
import com.pankajgadge.user.domain.usecase.GetQuizHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for quiz history screen
 * Handles loading and filtering quiz results
 */
@HiltViewModel
class QuizHistoryViewModel @Inject constructor(
    private val getQuizHistoryUseCase: GetQuizHistoryUseCase,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow<QuizHistoryUiState>(QuizHistoryUiState.Loading)
    val uiState: StateFlow<QuizHistoryUiState> = _uiState.asStateFlow()

    private val _filterState = MutableStateFlow(FilterState())
    val filterState: StateFlow<FilterState> = _filterState.asStateFlow()

    private var allResults: List<QuizResult> = emptyList()

    init {
        loadQuizHistory()
    }

    /**
     * Load quiz history from repository
     */
    fun loadQuizHistory() {
        val userId = firebaseAuth.currentUser?.uid ?: return

        viewModelScope.launch {
            _uiState.value = QuizHistoryUiState.Loading

            when (val result = getQuizHistoryUseCase(userId)) {
                is Result.Success -> {
                    allResults = result.data
                    applyFilters()
                }

                is Result.Error -> {
                    _uiState.value = QuizHistoryUiState.Error(
                        result.exception.message ?: "Failed to load quiz history"
                    )
                }

                is Result.Loading -> {
                    _uiState.value = QuizHistoryUiState.Loading
                }
            }
        }
    }
//    init {
//        val currentUser = firebaseAuth.currentUser
//        Log.d("QuizHistory", "Current user: ${currentUser?.uid}")
//        Log.d("QuizHistory", "Email: ${currentUser?.email}")
//
//        if (currentUser != null) {
//            loadQuizHistory()
//        } else {
//            Log.e("QuizHistory", "No user signed in!")
//        }
//    }
    /**
     * Update filter and apply
     */
    fun updateFilter(updater: (FilterState) -> FilterState) {
        _filterState.value = updater(_filterState.value)
        applyFilters()
    }

    /**
     * Clear all filters
     */
    fun clearFilters() {
        _filterState.value = FilterState()
        applyFilters()
    }

    /**
     * Apply current filters to results
     */
    private fun applyFilters() {
        val filter = _filterState.value
        var filtered = allResults

        // Filter by search query (quiz title)
        if (filter.searchQuery.isNotBlank()) {
            filtered = filtered.filter {
                it.quizTitle.contains(filter.searchQuery, ignoreCase = true)
            }
        }

        // Filter by minimum score
        if (filter.minScore > 0) {
            filtered = filtered.filter { it.earnedPoints >= filter.minScore }
        }

        // Sort based on selected option
        filtered = when (filter.sortBy) {
            SortOption.DATE_DESC -> filtered.sortedByDescending { it.completedAt }
            SortOption.DATE_ASC -> filtered.sortedBy { it.completedAt }
            SortOption.SCORE_DESC -> filtered.sortedByDescending { it.earnedPoints }
            SortOption.SCORE_ASC -> filtered.sortedBy { it.earnedPoints }
        }

        _uiState.value = if (filtered.isEmpty() && allResults.isNotEmpty()) {
            QuizHistoryUiState.Empty("No quizzes match your filters")
        } else if (filtered.isEmpty()) {
            QuizHistoryUiState.Empty("No quiz history yet. Start taking quizzes!")
        } else {
            QuizHistoryUiState.Success(
                results = filtered,
                totalQuizzes = allResults.size,
                averageScore = allResults.map { it.earnedPoints }.average().toInt(),
                highestScore = allResults.maxOfOrNull { it.earnedPoints } ?: 0
            )
        }
    }

    /**
     * Get detailed result by ID
     */
    fun getResultById(resultId: String): QuizResult? {
        return allResults.find { it.id == resultId }
    }
}

/**
 * UI state for quiz history screen
 */
sealed class QuizHistoryUiState {
    object Loading : QuizHistoryUiState()

    data class Success(
        val results: List<QuizResult>,
        val totalQuizzes: Int,
        val averageScore: Int,
        val highestScore: Int
    ) : QuizHistoryUiState()

    data class Empty(val message: String) : QuizHistoryUiState()
    data class Error(val message: String) : QuizHistoryUiState()
}

/**
 * Filter state for quiz history
 */
data class FilterState(
    val searchQuery: String = "",
    val minScore: Int = 0,
    val sortBy: SortOption = SortOption.DATE_DESC
)

/**
 * Sort options for quiz history
 */
enum class SortOption {
    DATE_DESC,    // Newest first
    DATE_ASC,     // Oldest first
    SCORE_DESC,   // Highest score first
    SCORE_ASC     // Lowest score first
}