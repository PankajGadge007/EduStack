package com.pankajgadge.quiz.presentation.taking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pankajgadge.core.common.result.Result
import com.pankajgadge.core.domain.model.Question
import com.pankajgadge.core.domain.model.Quiz
import com.pankajgadge.core.domain.model.QuizSubmission
import com.pankajgadge.core.domain.model.SubmitQuizRequest
import com.pankajgadge.core.domain.model.SubmitQuizResponse
import com.pankajgadge.core.domain.model.SubmittedAnswer
import com.pankajgadge.core.domain.repository.AuthSessionRepository
import com.pankajgadge.quiz.domain.usecase.GetQuizByIdUseCase
import com.pankajgadge.quiz.domain.usecase.SubmitQuizUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

/**
 * ViewModel for taking a quiz
 * Handles quiz loading, timer, answer selection, and submission
 */
@HiltViewModel
class QuizTakingViewModel @Inject constructor(
    private val getQuizByIdUseCase: GetQuizByIdUseCase,
    private val submitQuizUseCase: SubmitQuizUseCase,
    private val firebaseAuth: AuthSessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<QuizTakingUiState>(QuizTakingUiState.Loading)
    val uiState: StateFlow<QuizTakingUiState> = _uiState.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _timeRemaining = MutableStateFlow(0L)
    val timeRemaining: StateFlow<Long> = _timeRemaining.asStateFlow()

    private var quiz: Quiz? = null
    private var submission: QuizSubmission? = null
    private var timerJob: Job? = null
    private var startTime: Date? = null

    /**
     * Load quiz by ID
     */
    fun loadQuiz(quizId: String) {
        val userId = firebaseAuth.getCurrentUserId() ?: return

        viewModelScope.launch {
            _uiState.value = QuizTakingUiState.Loading

            when (val result = getQuizByIdUseCase(quizId)) {
                is Result.Success -> {
                    quiz = result.data
                    startTime = Date()

                    // Initialize submission
                    submission = QuizSubmission(
                        quizId = quizId,
                        userId = userId,
                        startedAt = startTime!!,
                        currentQuestionIndex = 0,
                        timeRemaining = result.data.duration * 60L // Convert minutes to seconds
                    )

                    _timeRemaining.value = result.data.duration * 60L
                    _currentQuestionIndex.value = 0

                    _uiState.value = QuizTakingUiState.InProgress(
                        quiz = result.data,
                        currentQuestion = result.data.questions.first(),
                        questionNumber = 1,
                        totalQuestions = result.data.questions.size,
                        selectedAnswer = null
                    )

                    // Start timer
                    startTimer()
                }

                is Result.Error -> {
                    _uiState.value = QuizTakingUiState.Error(
                        result.exception.message ?: "Failed to load quiz"
                    )
                }

                is Result.Loading -> {
                    _uiState.value = QuizTakingUiState.Loading
                }
            }
        }
    }

    /**
     * Select an answer for current question
     */
    fun selectAnswer(answerIndex: Int) {
        val currentState = _uiState.value
        if (currentState is QuizTakingUiState.InProgress) {
            _uiState.value = currentState.copy(selectedAnswer = answerIndex)
        }
    }

    /**
     * Move to next question
     */
    fun nextQuestion() {
        val currentState = _uiState.value
        if (currentState !is QuizTakingUiState.InProgress) return

        val currentQuiz = quiz ?: return
        val currentSubmission = submission ?: return

        // Save current answer
        currentState.selectedAnswer?.let { answer ->
            currentSubmission.answers[currentState.currentQuestion.id] = SubmittedAnswer(
                questionId = currentState.currentQuestion.id,
                selectedAnswer = answer.toString(),
                answeredAt = Date()
            )
        }

        val nextIndex = _currentQuestionIndex.value + 1

        if (nextIndex < currentQuiz.questions.size) {
            // Move to next question
            _currentQuestionIndex.value = nextIndex

            val nextQuestion = currentQuiz.questions[nextIndex]
            val savedAnswer =
                currentSubmission.answers[nextQuestion.id]?.selectedAnswer?.toIntOrNull()

            _uiState.value = QuizTakingUiState.InProgress(
                quiz = currentQuiz,
                currentQuestion = nextQuestion,
                questionNumber = nextIndex + 1,
                totalQuestions = currentQuiz.questions.size,
                selectedAnswer = savedAnswer
            )
        } else {
            // No more questions, ready to submit
            _uiState.value = QuizTakingUiState.ReadyToSubmit(
                quiz = currentQuiz,
                answeredQuestions = currentSubmission.answers.size,
                totalQuestions = currentQuiz.questions.size
            )
        }
    }

    /**
     * Move to previous question
     */
    fun previousQuestion() {
        val currentState = _uiState.value
        if (currentState !is QuizTakingUiState.InProgress) return

        val currentQuiz = quiz ?: return
        val currentSubmission = submission ?: return

        // Save current answer
        currentState.selectedAnswer?.let { answer ->
            currentSubmission.answers[currentState.currentQuestion.id] = SubmittedAnswer(
                questionId = currentState.currentQuestion.id,
                selectedAnswer = answer.toString(),
                answeredAt = Date()
            )
        }

        val previousIndex = _currentQuestionIndex.value - 1

        if (previousIndex >= 0) {
            _currentQuestionIndex.value = previousIndex

            val previousQuestion = currentQuiz.questions[previousIndex]
            val savedAnswer =
                currentSubmission.answers[previousQuestion.id]?.selectedAnswer?.toIntOrNull()

            _uiState.value = QuizTakingUiState.InProgress(
                quiz = currentQuiz,
                currentQuestion = previousQuestion,
                questionNumber = previousIndex + 1,
                totalQuestions = currentQuiz.questions.size,
                selectedAnswer = savedAnswer
            )
        }
    }

    /**
     * Submit quiz
     */
    fun submitQuiz() {
        val currentSubmission = submission ?: return
        val userId = firebaseAuth.getCurrentUserId() ?: return

        viewModelScope.launch {
            _uiState.value = QuizTakingUiState.Submitting

            // Stop timer
            timerJob?.cancel()

            val endTime = Date()
            val timeTaken = (endTime.time - startTime!!.time) / 1000 // Convert to seconds

            val request = SubmitQuizRequest(
                quizId = currentSubmission.quizId,
                userId = userId,
                answers = currentSubmission.answers,
                timeTaken = timeTaken,
                startedAt = currentSubmission.startedAt,
                completedAt = endTime
            )

            when (val result = submitQuizUseCase(request)) {
                is Result.Success -> {
                    _uiState.value = QuizTakingUiState.Submitted(result.data)
                }

                is Result.Error -> {
                    _uiState.value = QuizTakingUiState.Error(
                        result.exception.message ?: "Failed to submit quiz"
                    )
                }

                is Result.Loading -> {
                    _uiState.value = QuizTakingUiState.Submitting
                }
            }
        }
    }

    /**
     * Start countdown timer
     */
    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_timeRemaining.value > 0) {
                delay(1000)
                _timeRemaining.value -= 1

                // Auto-submit when time runs out
                if (_timeRemaining.value == 0L) {
                    submitQuiz()
                }
            }
        }
    }

    /**
     * Pause timer
     */
    fun pauseTimer() {
        timerJob?.cancel()
    }

    /**
     * Resume timer
     */
    fun resumeTimer() {
        startTimer()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}

/**
 * UI state for quiz taking screen
 */
sealed class QuizTakingUiState {
    object Loading : QuizTakingUiState()

    data class InProgress(
        val quiz: Quiz,
        val currentQuestion: Question,
        val questionNumber: Int,
        val totalQuestions: Int,
        val selectedAnswer: Int?
    ) : QuizTakingUiState()

    data class ReadyToSubmit(
        val quiz: Quiz,
        val answeredQuestions: Int,
        val totalQuestions: Int
    ) : QuizTakingUiState()

    object Submitting : QuizTakingUiState()

    data class Submitted(
        val response: SubmitQuizResponse
    ) : QuizTakingUiState()

    data class Error(val message: String) : QuizTakingUiState()
}