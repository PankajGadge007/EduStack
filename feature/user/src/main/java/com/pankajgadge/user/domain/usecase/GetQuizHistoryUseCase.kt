package com.pankajgadge.user.domain.usecase

import com.pankajgadge.core.common.result.Result
import com.pankajgadge.core.domain.model.QuizResult
import com.pankajgadge.core.domain.repository.UserProfileRepository
import java.lang.Exception
import javax.inject.Inject

/**
 * Use case for getting quiz history
 */
class GetQuizHistoryUseCase @Inject constructor(
    private val userProfileRepository: UserProfileRepository
) {
    suspend operator fun invoke(userId: String, limit: Int = 50): Result<List<QuizResult>> {
        if (userId.isBlank()) {
            return Result.Error(Exception("User ID cannot be empty"))
        }

        if (limit <= 0) {
            return Result.Error(Exception("Limit must be greater than 0"))
        }

        return userProfileRepository.getQuizHistory(userId, limit)
    }
}