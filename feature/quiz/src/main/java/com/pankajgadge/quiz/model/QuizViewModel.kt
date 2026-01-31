package com.pankajgadge.quiz.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pankajgadge.api.repository.QuizRepository
import com.pankajgadge.common.result.Result
import com.pankajgadge.domain.model.Quiz
import com.pankajgadge.firebase.di.FirebaseQuiz
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    @FirebaseQuiz private val quizRepository: QuizRepository  // ← Uses Firebase!
) : ViewModel() {

    private val _quizzes = MutableStateFlow<Result<List<Quiz>>>(Result.Loading)
    val quizzes: StateFlow<Result<List<Quiz>>> = _quizzes.asStateFlow()

    init {
        loadQuizzes()
    }

    fun loadQuizzes() {
        viewModelScope.launch {
            quizRepository.getQuizzes().collect { result ->
                _quizzes.value = result
            }
        }
    }

    fun submitQuiz(quizId: String, answers: Map<String, Int>) {
        viewModelScope.launch {
            when (val result = quizRepository.submitQuiz(quizId, answers)) {
                is Result.Success -> {
                    // Handle success - show score
                    val score = result.data
                    // TODO: Navigate to result screen or show dialog
                }
                is Result.Error -> {
                    // Handle error
                }
                is Result.Loading -> {
                    // Show loading
                }
            }
        }
    }
}