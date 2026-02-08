package com.pankajgadge.user.domain.usecase

import com.pankajgadge.core.common.result.Result
import com.pankajgadge.core.common.result.errorResult
import com.pankajgadge.core.domain.model.UserProfile
import com.pankajgadge.core.domain.repository.UserProfileRepository
import javax.inject.Inject

/**
 * Use case for getting user profile
 */
class GetUserProfileUseCase @Inject constructor(
    private val userProfileRepository: UserProfileRepository
) {
    suspend operator fun invoke(userId: String): Result<UserProfile> {
        if (userId.isBlank()) {
            return errorResult("User ID cannot be empty")
        }

        return userProfileRepository.getUserProfile(userId)
    }
}