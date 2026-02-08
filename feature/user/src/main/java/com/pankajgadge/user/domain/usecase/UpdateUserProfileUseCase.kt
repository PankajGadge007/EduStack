package com.pankajgadge.user.domain.usecase

import com.pankajgadge.core.common.result.Result
import com.pankajgadge.core.common.result.errorResult
import com.pankajgadge.core.domain.model.UserProfile
import com.pankajgadge.core.domain.repository.UserProfileRepository
import javax.inject.Inject

/**
 * Use case for updating user profile
 */
class UpdateUserProfileUseCase @Inject constructor(
    private val userProfileRepository: UserProfileRepository
) {
    suspend operator fun invoke(userProfile: UserProfile): Result<Unit> {
        // Validate profile data
        if (userProfile.name.isBlank()) {
            return errorResult("Name cannot be empty")
        }

        if (userProfile.email.isBlank()) {
            return errorResult("Email cannot be empty")
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(userProfile.email).matches()) {
            return errorResult("Invalid email format")
        }

        return userProfileRepository.updateUserProfile(userProfile)
    }
}