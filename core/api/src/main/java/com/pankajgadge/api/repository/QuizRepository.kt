package com.pankajgadge.api.repository


import com.pankajgadge.domain.model.Quiz
import kotlinx.coroutines.flow.Flow

// core-api/repository/QuizRepository.kt
interface QuizRepository {
    fun getQuizzes(): Flow<Result>
    suspend fun getQuizById(id: String): Result<Quiz>
    suspend fun submitQuiz(quizId: String, answers: Map<String, Int>): Result<Int>
}