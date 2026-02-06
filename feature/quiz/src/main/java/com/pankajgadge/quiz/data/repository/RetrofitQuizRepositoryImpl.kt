package com.pankajgadge.quiz.data.repository

import com.pankajgadge.domain.model.Quiz

import com.pankajgadge.common.result.Result
import com.pankajgadge.domain.repository.QuizRepository
import com.pankajgadge.quiz.data.remote.QuizApiService
import javax.inject.Inject

/**
 * Retrofit-based implementation of QuizRepository.
 * Uses actual API calls to fetch quiz data from the backend.
 */
class RetrofitQuizRepositoryImpl()

//class RetrofitQuizRepositoryImpl @Inject constructor(
//    private val apiService: QuizApiService
//) : QuizRepository {
//
//    override suspend fun getQuizzes(): Result<List<Quiz>> {
//        return try {
//            val response = apiService.getQuizzes()
//            if (response.isSuccessful) {
//                val quizzes = response.body() ?: emptyList()
//                Result.Success(quizzes)
//            } else {
//                Result.Error("Failed to fetch quizzes: ${response.code()} ${response.message()}")
//            }
//        } catch (e: Exception) {
//            Result.Error("Network error: ${e.message ?: "Unknown error"}")
//        }
//    }
//
//    override suspend fun getQuizById(quizId: String): Result<Quiz> {
//        return try {
//            val response = apiService.getQuizById(quizId)
//            if (response.isSuccessful && response.body() != null) {
//                Result.Success(response.body()!!)
//            } else {
//                Result.Error("Quiz not found: ${response.code()} ${response.message()}")
//            }
//        } catch (e: Exception) {
//            Result.Error("Network error: ${e.message ?: "Unknown error"}")
//        }
//    }
//
//    override suspend fun submitQuizAnswers(
//        quizId: String,
//        answers: Map<String, String>
//    ): Result<QuizResult> {
//        return try {
//            val response = apiService.submitQuizAnswers(quizId, answers)
//            if (response.isSuccessful && response.body() != null) {
//                Result.Success(response.body()!!)
//            } else {
//                Result.Error("Failed to submit answers: ${response.code()} ${response.message()}")
//            }
//        } catch (e: Exception) {
//            Result.Error("Network error: ${e.message ?: "Unknown error"}")
//        }
//    }
//
//    override suspend fun getQuizHistory(): Result<List<QuizResult>> {
//        return try {
//            val response = apiService.getQuizHistory()
//            if (response.isSuccessful) {
//                val history = response.body() ?: emptyList()
//                Result.Success(history)
//            } else {
//                Result.Error("Failed to fetch quiz history: ${response.code()} ${response.message()}")
//            }
//        } catch (e: Exception) {
//            Result.Error("Network error: ${e.message ?: "Unknown error"}")
//        }
//    }
//
//    override suspend fun getQuizzesByCategory(category: String): Result<List<Quiz>> {
//        return try {
//            val response = apiService.getQuizzesByCategory(category)
//            if (response.isSuccessful) {
//                val quizzes = response.body() ?: emptyList()
//                Result.Success(quizzes)
//            } else {
//                Result.Error("Failed to fetch quizzes: ${response.code()} ${response.message()}")
//            }
//        } catch (e: Exception) {
//            Result.Error("Network error: ${e.message ?: "Unknown error"}")
//        }
//    }
//}