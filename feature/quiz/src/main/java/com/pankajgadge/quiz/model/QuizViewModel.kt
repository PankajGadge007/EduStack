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
    @FirebaseQuiz private val quizRepository: QuizRepository
) : ViewModel() {

    private val _quizzesState = MutableStateFlow<Result<List<Quiz>>>(Result.Loading)
    val quizzesState: StateFlow<Result<List<Quiz>>> = _quizzesState.asStateFlow()

    private val _selectedQuiz = MutableStateFlow<Result<Quiz>>(Result.Loading)
    val selectedQuiz: StateFlow<Result<Quiz>> = _selectedQuiz.asStateFlow()

    private val _submitState = MutableStateFlow<Result<Int>?>(null)
    val submitState: StateFlow<Result<Int>?> = _submitState.asStateFlow()

    private val _answers = MutableStateFlow<Map<String, Int>>(emptyMap())
    val answers: StateFlow<Map<String, Int>> = _answers.asStateFlow()

    init {
        loadQuizzes()
    }

    fun loadQuizzes() {
        viewModelScope.launch {
            quizRepository.getQuizzes().collect { result ->
                _quizzesState.value = result
            }
        }
    }

    fun loadQuiz(quizId: String) {
        viewModelScope.launch {
            _selectedQuiz.value = Result.Loading
            _selectedQuiz.value = quizRepository.getQuizById(quizId)
        }
    }

    fun selectAnswer(questionId: String, answerIndex: Int) {
        _answers.value = _answers.value.toMutableMap().apply {
            put(questionId, answerIndex)
        }
    }

    fun submitQuiz(quizId: String) {
        viewModelScope.launch {
            _submitState.value = Result.Loading
            _submitState.value = quizRepository.submitQuiz(quizId, _answers.value)
        }
    }

    fun resetAnswers() {
        _answers.value = emptyMap()
        _submitState.value = null
    }
}
