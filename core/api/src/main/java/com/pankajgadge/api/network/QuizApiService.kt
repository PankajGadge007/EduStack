package com.pankajgadge.api.network

import com.pankajgadge.database.dto.QuizDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface QuizApiService {
    @GET("quizzes")
    suspend fun getQuizzes(): List<QuizDto>

    @GET("quizzes/{id}")
    suspend fun getQuizById(@Path("id") id: String): QuizDto

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