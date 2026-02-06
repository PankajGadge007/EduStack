package com.pankajgadge.firebase.repository


import com.google.firebase.auth.FirebaseAuth
import com.pankajgadge.common.result.Result
import com.pankajgadge.domain.model.Quiz
import com.pankajgadge.domain.repository.QuizRepository
import com.pankajgadge.firebase.datasource.FirebaseQuizDataSource
import com.pankajgadge.firebase.mapper.QuizMapper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FirebaseQuizRepositoryImpl @Inject constructor(
    private val dataSource: FirebaseQuizDataSource,
    private val mapper: QuizMapper,
    private val auth: FirebaseAuth
) : QuizRepository {

    override fun getQuizzes(): Flow<Result<List<Quiz>>> = flow {
        emit(Result.Loading)
        try {
            val firebaseQuizzes = dataSource.getQuizzes()
            val domainQuizzes = firebaseQuizzes.map { mapper.toDomain(it) }
            emit(Result.Success(domainQuizzes))
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }

    override suspend fun getQuizById(id: String): Result<Quiz> {
        return try {
            val firebaseQuiz = dataSource.getQuizById(id)
            val domainQuiz = mapper.toDomain(firebaseQuiz)
            Result.Success(domainQuiz)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun submitQuiz(
        quizId: String,
        answers: Map<String, Int>
    ): Result<Int> {
        return try {
            val userId = auth.currentUser?.uid
                ?: throw Exception("User not logged in")

            val quiz = dataSource.getQuizById(quizId)

            // Calculate score
            var score = 0
            var totalPoints = 0

            quiz.questions.forEach { question ->
                totalPoints += question.points
                val userAnswer = answers[question.id]
                if (userAnswer == question.correctAnswer) {
                    score += question.points
                }
            }

            // Save submission
            dataSource.saveSubmission(userId, quizId, score, totalPoints, answers)

            Result.Success(score)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}