package com.pankajgadge.quiz.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pankajgadge.common.result.Result
import com.pankajgadge.domain.model.Question
import com.pankajgadge.domain.model.Quiz
import com.pankajgadge.domain.repository.QuizRepository
import com.pankajgadge.quiz.domain.SubmitQuizUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val quizRepository: QuizRepository,
    private val submitQuizUseCase: SubmitQuizUseCase
) : ViewModel() {

    private val quizId: String = checkNotNull(savedStateHandle["quizId"])

    private val _uiState = MutableStateFlow<QuizDetailUiState>(QuizDetailUiState.Loading)
    val uiState: StateFlow<QuizDetailUiState> = _uiState.asStateFlow()

    private val _answers = MutableStateFlow<Map<String, Int>>(emptyMap())
    val answers: StateFlow<Map<String, Int>> = _answers.asStateFlow()

    init {
        loadQuiz()
    }

    private fun loadQuiz() {
        viewModelScope.launch {
            when (val result = quizRepository.getQuizById(quizId)) {
                is Result.Success -> {
                    _uiState.value = QuizDetailUiState.Success(result.data)
                }
                is Result.Error -> {
                    _uiState.value = QuizDetailUiState.Error(
                        result.exception.message ?: "Failed to load quiz"
                    )
                }
                is Result.Loading -> {
                    _uiState.value = QuizDetailUiState.Loading
                }
            }
        }
    }

    fun selectAnswer(questionId: String, answerIndex: Int) {
        _answers.value = _answers.value.toMutableMap().apply {
            put(questionId, answerIndex)
        }
    }

    fun submitQuiz() {
        viewModelScope.launch {
            _uiState.value = QuizDetailUiState.Submitting

            when (val result = submitQuizUseCase(quizId, _answers.value)) {
                is Result.Success -> {
                    _uiState.value = QuizDetailUiState.Submitted(result.data)
                }
                is Result.Error -> {
                    _uiState.value = QuizDetailUiState.Error(
                        result.exception.message ?: "Failed to submit quiz"
                    )
                }
                is Result.Loading -> { /* Already in submitting state */ }
            }
        }
    }
}

sealed class QuizDetailUiState {
    object Loading : QuizDetailUiState()
    data class Success(val quiz: Quiz) : QuizDetailUiState()
    object Submitting : QuizDetailUiState()
    data class Submitted(val score: Int) : QuizDetailUiState()
    data class Error(val message: String) : QuizDetailUiState()
}