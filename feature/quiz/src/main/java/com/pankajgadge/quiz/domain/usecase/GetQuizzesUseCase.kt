package com.pankajgadge.quiz.domain.usecase

import com.pankajgadge.core.domain.model.Quiz
import com.pankajgadge.core.common.result.Result
import com.pankajgadge.core.domain.repository.QuizRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetQuizzesUseCase @Inject constructor(
    private val repository: QuizRepository
) {
    operator fun invoke(): Flow<Result<List<Quiz>>> {
        return repository.getQuizzes()
    }
}