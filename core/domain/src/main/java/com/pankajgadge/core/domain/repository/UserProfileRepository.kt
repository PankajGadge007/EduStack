package com.pankajgadge.core.domain.repository

import com.pankajgadge.core.common.result.Result
import com.pankajgadge.core.domain.model.QuizResult
import com.pankajgadge.core.domain.model.QuizStats
import com.pankajgadge.core.domain.model.UserProfile

/**
 * Repository interface for user profile operations
 * Handles user data storage and retrieval from Firestore
 */
interface UserProfileRepository {

    /**
     * Get user profile by ID
     * @param userId User's unique ID
     * @return Result with UserProfile or error
     */
    suspend fun getUserProfile(userId: String): Result<UserProfile>

    /**
     * Update user profile
     * @param userProfile Updated user profile data
     * @return Result indicating success or failure
     */
    suspend fun updateUserProfile(userProfile: UserProfile): Result<Unit>

    /**
     * Get quiz history for a user
     * @param userId User's unique ID
     * @param limit Maximum number of results to return
     * @return Result with list of QuizResult
     */
    suspend fun getQuizHistory(userId: String, limit: Int = 50): Result<List<QuizResult>>

    /**
     * Get quiz statistics for a user
     * @param userId User's unique ID
     * @return Result with QuizStats
     */
    suspend fun getQuizStats(userId: String): Result<QuizStats>

    /**
     * Save quiz result
     * @param quizResult Quiz result to save
     * @return Result indicating success or failure
     */
    suspend fun saveQuizResult(quizResult: QuizResult): Result<QuizResult>

    /**
     * Delete quiz result
     * @param resultId Quiz result ID to delete
     * @return Result indicating success or failure
     */
    suspend fun deleteQuizResult(resultId: String): Result<Unit>

    /**
     * Get leaderboard (top scorers)
     * @param limit Maximum number of users to return
     * @return Result with list of UserProfile sorted by score
     */
    suspend fun getLeaderboard(limit: Int = 10): Result<List<UserProfile>>
}