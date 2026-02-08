package com.pankajgadge.auth.domain.usecase

import android.util.Patterns
import com.pankajgadge.core.common.auth.AuthResult
import com.pankajgadge.core.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Use case for sending password reset email
 * Validates email and delegates to repository
 */
class ForgotPasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    /**
     * Send password reset email
     * @param email User's email address
     * @return AuthResult indicating success or failure
     */
    suspend operator fun invoke(email: String): AuthResult {
        // Validate email is not blank
        if (email.isBlank()) {
            return AuthResult.Error("Email cannot be empty")
        }

        // Validate email format
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return AuthResult.Error("Please enter a valid email address")
        }

        // Send reset email
        return authRepository.sendPasswordResetEmail(email)
    }
}