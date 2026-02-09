package com.pankajgadge.core.impl.repository

import com.pankajgadge.core.impl.network.RetrofitQuizApiService
import com.pankajgadge.core.common.result.Result
import com.pankajgadge.core.database.dao.QuizDao
import com.pankajgadge.core.database.dto.QuizDto
import com.pankajgadge.core.domain.model.Question
import com.pankajgadge.core.domain.model.Quiz
import com.pankajgadge.core.domain.repository.QuizRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitQuizRepositoryImpl @Inject constructor(
    private val api: RetrofitQuizApiService,
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