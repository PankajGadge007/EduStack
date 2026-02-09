package com.pankajgadge.user.domain.usecase

import com.pankajgadge.core.common.result.Result
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
            return Result.Error(Exception("Name cannot be empty"))
        }

        if (userProfile.email.isBlank()) {
            return Result.Error(Exception("Email cannot be empty"))
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(userProfile.email).matches()) {
            return Result.Error(Exception("Invalid email format"))
        }

        return userProfileRepository.updateUserProfile(userProfile)
    }
}