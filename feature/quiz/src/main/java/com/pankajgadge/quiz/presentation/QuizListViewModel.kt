package com.pankajgadge.quiz.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pankajgadge.common.result.Result
import com.pankajgadge.domain.model.Quiz
import com.pankajgadge.quiz.domain.GetQuizzesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizListViewModel @Inject constructor(
    private val getQuizzesUseCase: GetQuizzesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<QuizListUiState>(QuizListUiState.Loading)
    val uiState: StateFlow<QuizListUiState> = _uiState.asStateFlow()

    init {
        loadQuizzes()
    }

    fun loadQuizzes() {
        viewModelScope.launch {
            getQuizzesUseCase().collect { result ->
                _uiState.value = when (result) {
                    is Result.Loading -> QuizListUiState.Loading
                    is Result.Success -> QuizListUiState.Success(result.data)
                    is Result.Error -> QuizListUiState.Error(
                        result.exception.message ?: "Unknown error"
                    )
                }
            }
        }
    }
}

sealed class QuizListUiState {
    object Loading : QuizListUiState()
    data class Success(val quizzes: List<Quiz>) : QuizListUiState()
    data class Error(val message: String) : QuizListUiState()
}