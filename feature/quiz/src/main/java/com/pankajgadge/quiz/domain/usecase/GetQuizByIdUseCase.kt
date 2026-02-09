package com.pankajgadge.quiz.domain.usecase

import com.pankajgadge.core.common.result.Result
import com.pankajgadge.core.domain.model.Quiz
import com.pankajgadge.core.domain.repository.QuizRepository
import java.lang.Exception
import javax.inject.Inject

/**
 * Use case for getting a quiz by ID
 */
class GetQuizByIdUseCase @Inject constructor(
    private val quizRepository: QuizRepository
) {
    suspend operator fun invoke(quizId: String): Result<Quiz> {
        if (quizId.isBlank()) {
            return Result.Error(Exception("Quiz ID cannot be empty"))
        }

        return quizRepository.getQuizById(quizId)
    }
}