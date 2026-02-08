package com.pankajgadge.core.api.repository

import com.pankajgadge.core.domain.model.Quiz
import com.pankajgadge.core.common.result.Result
import kotlinx.coroutines.flow.Flow

interface QuizRepository {
    fun getQuizzes(): Flow<Result<List<Quiz>>>
    suspend fun getQuizById(id: String): Result<Quiz>
    suspend fun submitQuiz(quizId: String, answers: Map<String, Int>): Result<Int>
}