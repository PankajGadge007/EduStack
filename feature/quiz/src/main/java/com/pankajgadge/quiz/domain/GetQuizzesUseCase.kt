package com.pankajgadge.quiz.domain

import com.pankajgadge.common.result.Result
import com.pankajgadge.domain.model.Quiz
import com.pankajgadge.domain.repository.QuizRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetQuizzesUseCase @Inject constructor(
    private val repository: QuizRepository
) {
    operator fun invoke(): Flow<Result<List<Quiz>>> {
        return repository.getQuizzes()
    }
}
