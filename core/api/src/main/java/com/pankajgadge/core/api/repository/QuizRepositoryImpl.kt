package com.pankajgadge.core.api.repository

import com.pankajgadge.database.dao.QuizDao
import com.pankajgadge.core.domain.model.Quiz
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject


import com.pankajgadge.core.api.network.QuizApiService
import com.pankajgadge.core.database.dto.QuizDto
import com.pankajgadge.core.domain.model.Question


class QuizRepositoryImpl @Inject constructor(
    private val api: QuizApiService,
    private val quizDao: QuizDao
) : QuizRepository {

    override fun getQuizzes(): Flow<Result<List<Quiz>>> = flow {
        emit(Result.Loading)
        try {
            val quizzes = api.getQuizzes().map { it.toDomainModel() }
            emit(Result.Success(quizzes))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    override suspend fun getQuizById(id: String): Result<Quiz> {
        return try {
            val quiz = api.getQuizById(id).toDomainModel()
            Result.Success(quiz)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun submitQuiz(
        quizId: String,
        answers: Map<String, Int>
    ): Result<Int> {
        return try {
            val response = api.submitQuiz(quizId, answers)
            Result.Success(response.score)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

// Extension function
private fun QuizDto.toDomainModel() = Quiz(
    id = id,
    title = title,
    description = description,
    questions = questions.map {
        Question(it.id, it.text, it.options, it.correctAnswer, it.points)
    },
    duration = duration,
    createdBy = createdBy,
    createdAt = createdAt
)