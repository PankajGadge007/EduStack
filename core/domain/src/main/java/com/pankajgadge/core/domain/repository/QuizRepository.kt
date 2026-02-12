package com.pankajgadge.core.domain.repository

import com.pankajgadge.core.common.result.Result
import com.pankajgadge.core.domain.model.Quiz
import kotlinx.coroutines.flow.Flow

interface QuizRepository {
    fun getQuizzes(): Flow<Result<List<Quiz>>>
    suspend fun getQuizById(quizId: String): Result<Quiz>
    suspend fun submitQuiz(quizId: String, answers: Map<String, Int>): Result<Int>
}