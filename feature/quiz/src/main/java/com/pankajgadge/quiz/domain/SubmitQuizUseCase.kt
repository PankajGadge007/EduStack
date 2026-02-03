package com.pankajgadge.quiz.domain

import com.pankajgadge.common.result.Result
import com.pankajgadge.domain.repository.QuizRepository
import javax.inject.Inject

class SubmitQuizUseCase @Inject constructor(
    private val repository: QuizRepository
) {
    suspend operator fun invoke(
        quizId: String,
        answers: Map<String, Int>
    ): Result<Int> {
        return repository.submitQuiz(quizId, answers)
    }
}