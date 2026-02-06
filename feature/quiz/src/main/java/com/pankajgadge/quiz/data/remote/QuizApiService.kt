package com.pankajgadge.quiz.data.remote

import com.pankajgadge.domain.model.Quiz
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit API service interface for quiz-related endpoints.
 * Define all API endpoints related to quizzes here.
 */
interface QuizApiService {

    /**
     * Get all available quizzes
     */
    @GET("quizzes")
    suspend fun getQuizzes(): Response<List<Quiz>>

    /**
     * Get quizzes by category
     */
    @GET("quizzes")
    suspend fun getQuizzesByCategory(
        @Query("category") category: String
    ): Response<List<Quiz>>

    /**
     * Get a specific quiz by ID
     */
    @GET("quizzes/{id}")
    suspend fun getQuizById(
        @Path("id") quizId: String
    ): Response<Quiz>

//    /**
//     * Submit quiz answers and get result
//     */
//    @POST("quizzes/{id}/submit")
//    suspend fun submitQuizAnswers(
//        @Path("id") quizId: String,
//        @Body answers: Map<String, String>
//    ): Response<QuizResult>
//
//    /**
//     * Get user's quiz history
//     */
//    @GET("quizzes/history")
//    suspend fun getQuizHistory(): Response<List<QuizResult>>

//    @GET("quizzes")
//    suspend fun getQuizzes(): List<QuizDto>
//
//    @GET("quizzes/{id}")
//    suspend fun getQuizById(@Path("id") id: String): QuizDto

    @POST("quizzes/{id}/submit")
    suspend fun submitQuiz(
        @Path("id") quizId: String,
        @Body answers: Map<String, Int>
    ): SubmitResponse

    @POST("auth/login")
    suspend fun login(@Body credentials: LoginRequest): LoginResponse
}

data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val user: UserDto, val token: String)
data class UserDto(val id: String, val name: String, val email: String, val role: String)
data class SubmitResponse(val score: Int, val totalPoints: Int)