package com.pankajgadge.auth.domain.usecase

import com.pankajgadge.common.auth.AuthResult
import com.pankajgadge.domain.repository.AuthRepository
import javax.inject.Inject

class GoogleSignInUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(idToken: String): AuthResult {
        if (idToken.isBlank()) {
            return AuthResult.Error("Invalid Google token")
        }

        return authRepository.signInWithGoogle(idToken)
    }
}