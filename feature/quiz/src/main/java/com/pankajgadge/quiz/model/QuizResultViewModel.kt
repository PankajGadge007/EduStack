package com.pankajgadge.quiz.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pankajgadge.core.domain.repository.AuthSessionRepository
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
 * ViewModel for Quiz Result Screen
 */
@HiltViewModel
class QuizResultViewModel @Inject constructor(
    private val getQuizHistoryUseCase: GetQuizHistoryUseCase,
    private val authSessionRepository: AuthSessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<QuizResultUiState>(QuizResultUiState.Loading)
    val uiState: StateFlow<QuizResultUiState> = _uiState.asStateFlow()

    fun loadResult(resultId: String) {
        val userId = authSessionRepository.getCurrentUserId() ?: return

        viewModelScope.launch {
            _uiState.value = QuizResultUiState.Loading

            when (val result = getQuizHistoryUseCase(userId)) {
                is Result.Success -> {
                    // Find the specific result
                    val quizResult = result.data.find { it.id == resultId }
                    if (quizResult != null) {
                        _uiState.value = QuizResultUiState.Success(quizResult)
                    } else {
                        _uiState.value = QuizResultUiState.Error("Quiz result not found")
                    }
                }
                is Result.Error -> {
                    _uiState.value = QuizResultUiState.Error(
                        result.exception.message ?: "Failed to load result"
                    )
                }
                is Result.Loading -> {
                    _uiState.value = QuizResultUiState.Loading
                }
            }
        }
    }
}

sealed class QuizResultUiState {
    object Loading : QuizResultUiState()
    data class Success(val result: QuizResult) : QuizResultUiState()
    data class Error(val message: String) : QuizResultUiState()
}