package com.pankajgadge.core.firebase.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pankajgadge.core.common.result.Result
import com.pankajgadge.core.domain.model.*
import com.pankajgadge.core.domain.repository.UserProfileRepository
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firebase implementation of UserProfileRepository
 * Handles user profile data in Firestore
 */
@Singleton
class FirebaseUserProfileRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
) : UserProfileRepository {

    companion object {
        private const val USERS_COLLECTION = "users"
        private const val QUIZ_RESULTS_COLLECTION = "quiz_results"
    }

    override suspend fun getUserProfile(userId: String): Result<UserProfile> {
        return try {
            val docSnapshot = firestore.collection(USERS_COLLECTION)
                .document(userId)
                .get()
                .await()

            if (!docSnapshot.exists()) {
                // Create default profile if doesn't exist
                val currentUser = firebaseAuth.currentUser
                val defaultProfile = UserProfile(
                    id = userId,
                    name = currentUser?.displayName ?: "User",
                    email = currentUser?.email ?: "",
                    role = UserRole.STUDENT,
                    photoUrl = currentUser?.photoUrl?.toString(),
                    joinedAt = Date(),
                    isEmailVerified = currentUser?.isEmailVerified ?: false
                )

                // Save default profile
                firestore.collection(USERS_COLLECTION)
                    .document(userId)
                    .set(defaultProfile.toFirestoreMap())
                    .await()

                return Result.Success(defaultProfile)
            }

            val data = docSnapshot.data ?: return Result.Error(Exception("Profile data is null"))
            val profile = data.toUserProfile(userId)

            Result.Success(profile)
        } catch (e: Exception) {
            Result.Error(Exception(e))
        }
    }

    override suspend fun updateUserProfile(userProfile: UserProfile): Result<Unit> {
        return try {
            firestore.collection(USERS_COLLECTION)
                .document(userProfile.id)
                .set(userProfile.toFirestoreMap())
                .await()

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(Exception(e))
        }
    }

    override suspend fun getQuizHistory(userId: String, limit: Int): Result<List<QuizResult>> {
        return try {
            val querySnapshot = firestore.collection(QUIZ_RESULTS_COLLECTION)
                .whereEqualTo("userId", userId)
                .orderBy("completedAt", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()

            val results = querySnapshot.documents.mapNotNull { doc ->
                try {
                    doc.data?.toQuizResult(doc.id)
                } catch (e: Exception) {
                    null
                }
            }

            Result.Success(results)
        } catch (e: Exception) {
            Result.Error(Exception(e))
        }
    }

    override suspend fun getQuizStats(userId: String): Result<QuizStats> {
        return try {
            // Get all quiz results for the user
            val querySnapshot = firestore.collection(QUIZ_RESULTS_COLLECTION)
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val results = querySnapshot.documents.mapNotNull { doc ->
                try {
                    doc.data?.toQuizResult(doc.id)
                } catch (e: Exception) {
                    null
                }
            }

            if (results.isEmpty()) {
                return Result.Success(QuizStats())
            }

            // Calculate statistics
            val stats = QuizStats(
                totalQuizzesTaken = results.size,
                totalScore = results.sumOf { it.earnedPoints },
                averageScore = results.map { it.earnedPoints }.average(),
                highestScore = results.maxOfOrNull { it.earnedPoints } ?: 0,
                totalTimePlayed = results.sumOf { it.timeTaken },
                lastQuizDate = results.maxOfOrNull { it.completedAt }
            )

            Result.Success(stats)
        } catch (e: Exception) {
            Result.Error(Exception(e))
        }
    }

    override suspend fun saveQuizResult(quizResult: QuizResult): Result<Unit> {
        return try {
            val docRef = if (quizResult.id.isEmpty()) {
                firestore.collection(QUIZ_RESULTS_COLLECTION).document()
            } else {
                firestore.collection(QUIZ_RESULTS_COLLECTION).document(quizResult.id)
            }

            val resultWithId = if (quizResult.id.isEmpty()) {
                quizResult.copy(id = docRef.id)
            } else {
                quizResult
            }

            docRef.set(resultWithId.toFirestoreMap()).await()

            // Update user stats
            updateUserStats(quizResult.userId)

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(Exception(e))
        }
    }

    override suspend fun deleteQuizResult(resultId: String): Result<Unit> {
        return try {
            val docSnapshot = firestore.collection(QUIZ_RESULTS_COLLECTION)
                .document(resultId)
                .get()
                .await()

            val userId = docSnapshot.getString("userId")

            firestore.collection(QUIZ_RESULTS_COLLECTION)
                .document(resultId)
                .delete()
                .await()

            // Update user stats if userId exists
            if (userId != null) {
                updateUserStats(userId)
            }

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(Exception(e))
        }
    }

    override suspend fun getLeaderboard(limit: Int): Result<List<UserProfile>> {
        return try {
            val querySnapshot = firestore.collection(USERS_COLLECTION)
                .orderBy("stats.totalScore", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()

            val profiles = querySnapshot.documents.mapNotNull { doc ->
                try {
                    doc.data?.toUserProfile(doc.id)
                } catch (e: Exception) {
                    null
                }
            }

            Result.Success(profiles)
        } catch (e: Exception) {
            Result.Error(Exception(e))
        }
    }

    /**
     * Update user statistics in Firestore
     */
    private suspend fun updateUserStats(userId: String) {
        try {
            val statsResult = getQuizStats(userId)
            if (statsResult is Result.Success) {
                firestore.collection(USERS_COLLECTION)
                    .document(userId)
                    .update("stats", statsResult.data.toFirestoreMap())
                    .await()
            }
        } catch (e: Exception) {
            // Log error but don't fail the operation
            android.util.Log.e("UserProfileRepo", "Failed to update stats", e)
        }
    }

    /**
     * Extension functions for mapping
     */
    private fun UserProfile.toFirestoreMap(): Map<String, Any?> {
        return mapOf(
            "name" to name,
            "email" to email,
            "role" to role.name,
            "photoUrl" to photoUrl,
            "phone" to phone,
            "bio" to bio,
            "institution" to institution,
            "grade" to grade,
            "subject" to subject,
            "joinedAt" to joinedAt,
            "stats" to stats.toFirestoreMap(),
            "isEmailVerified" to isEmailVerified
        )
    }

    private fun QuizStats.toFirestoreMap(): Map<String, Any?> {
        return mapOf(
            "totalQuizzesTaken" to totalQuizzesTaken,
            "totalScore" to totalScore,
            "averageScore" to averageScore,
            "highestScore" to highestScore,
            "totalTimePlayed" to totalTimePlayed,
            "lastQuizDate" to lastQuizDate
        )
    }

    private fun Map<String, Any?>.toUserProfile(userId: String): UserProfile {
        @Suppress("UNCHECKED_CAST")
        val statsMap = this["stats"] as? Map<String, Any?> ?: emptyMap()

        return UserProfile(
            id = userId,
            name = this["name"] as? String ?: "",
            email = this["email"] as? String ?: "",
            role = UserRole.valueOf(this["role"] as? String ?: "STUDENT"),
            photoUrl = this["photoUrl"] as? String,
            phone = this["phone"] as? String,
            bio = this["bio"] as? String,
            institution = this["institution"] as? String,
            grade = this["grade"] as? String,
            subject = this["subject"] as? String,
            joinedAt = (this["joinedAt"] as? com.google.firebase.Timestamp)?.toDate() ?: Date(),
            stats = statsMap.toQuizStats(),
            isEmailVerified = this["isEmailVerified"] as? Boolean ?: false
        )
    }

    private fun Map<String, Any?>.toQuizStats(): QuizStats {
        return QuizStats(
            totalQuizzesTaken = (this["totalQuizzesTaken"] as? Long)?.toInt() ?: 0,
            totalScore = (this["totalScore"] as? Long)?.toInt() ?: 0,
            averageScore = this["averageScore"] as? Double ?: 0.0,
            highestScore = (this["highestScore"] as? Long)?.toInt() ?: 0,
            totalTimePlayed = this["totalTimePlayed"] as? Long ?: 0L,
            lastQuizDate = (this["lastQuizDate"] as? com.google.firebase.Timestamp)?.toDate()
        )
    }

    private fun QuizResult.toFirestoreMap(): Map<String, Any?> {
        return mapOf(
            "userId" to userId,
            "quizId" to quizId,
            "quizTitle" to quizTitle,
            "score" to score,
            "totalQuestions" to totalQuestions,
            "correctAnswers" to correctAnswers,
            "wrongAnswers" to wrongAnswers,
            "totalPoints" to totalPoints,
            "earnedPoints" to earnedPoints,
            "timeTaken" to timeTaken,
            "completedAt" to completedAt,
            "answers" to answers.mapValues { it.value.toFirestoreMap() }
        )
    }

    private fun QuestionAnswer.toFirestoreMap(): Map<String, Any?> {
        return mapOf(
            "questionId" to questionId,
            "questionType" to questionType.name,
            "userAnswer" to userAnswer,
            "correctAnswer" to correctAnswer,
            "isCorrect" to isCorrect,
            "pointsEarned" to pointsEarned
        )
    }

    @Suppress("UNCHECKED_CAST")
    private fun Map<String, Any?>.toQuizResult(id: String): QuizResult {
        val answersMap = this["answers"] as? Map<String, Map<String, Any?>> ?: emptyMap()

        return QuizResult(
            id = id,
            userId = this["userId"] as? String ?: "",
            quizId = this["quizId"] as? String ?: "",
            quizTitle = this["quizTitle"] as? String ?: "",
            score = (this["score"] as? Long)?.toInt() ?: 0,
            totalQuestions = (this["totalQuestions"] as? Long)?.toInt() ?: 0,
            correctAnswers = (this["correctAnswers"] as? Long)?.toInt() ?: 0,
            wrongAnswers = (this["wrongAnswers"] as? Long)?.toInt() ?: 0,
            totalPoints = (this["totalPoints"] as? Long)?.toInt() ?: 0,
            earnedPoints = (this["earnedPoints"] as? Long)?.toInt() ?: 0,
            timeTaken = this["timeTaken"] as? Long ?: 0L,
            completedAt = (this["completedAt"] as? com.google.firebase.Timestamp)?.toDate() ?: Date(),
            answers = answersMap.mapValues { it.value.toQuestionAnswer() }
        )
    }

    private fun Map<String, Any?>.toQuestionAnswer(): QuestionAnswer {
        return QuestionAnswer(
            questionId = this["questionId"] as? String ?: "",
            questionType = QuestionType.valueOf(this["questionType"] as? String ?: "MULTIPLE_CHOICE"),
            userAnswer = this["userAnswer"] as? String ?: "",
            correctAnswer = this["correctAnswer"] as? String ?: "",
            isCorrect = this["isCorrect"] as? Boolean ?: false,
            pointsEarned = (this["pointsEarned"] as? Long)?.toInt() ?: 0
        )
    }
}