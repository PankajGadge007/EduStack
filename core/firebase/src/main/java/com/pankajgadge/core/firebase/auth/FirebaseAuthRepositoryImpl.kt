package com.pankajgadge.core.firebase.auth

import com.pankajgadge.core.common.auth.AuthResult
import com.pankajgadge.core.domain.repository.AuthRepository
import com.pankajgadge.core.firebase.datasource.FirebaseAuthDataSource
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Firebase implementation of AuthRepository
 * Handles authentication logic using Firebase Authentication
 */
@Singleton
class FirebaseAuthRepositoryImpl @Inject constructor(
    private val firebaseAuthDataSource: FirebaseAuthDataSource
) : AuthRepository {

    override fun isLoggedIn(): Boolean {
        return firebaseAuthDataSource.isUserSignedIn()
    }

    override suspend fun signIn(email: String, password: String): AuthResult {
        return try {
            // Validate inputs
            if (email.isBlank()) {
                return AuthResult.Error("Email cannot be empty")
            }
            if (password.isBlank()) {
                return AuthResult.Error("Password cannot be empty")
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                return AuthResult.Error("Please enter a valid email address")
            }

            // Sign in with Firebase
            val user = firebaseAuthDataSource.signInWithEmail(email, password)
            val displayName = user.displayName ?: user.email ?: "User"

            AuthResult.Success(displayName)
        } catch (e: Exception) {
            AuthResult.Error(
                e.message ?: "Sign in failed. Please check your credentials."
            )
        }
    }

    override suspend fun signUp(email: String, password: String, name: String): AuthResult {
        return try {
            // Validate inputs
            if (email.isBlank()) {
                return AuthResult.Error("Email cannot be empty")
            }
            if (password.isBlank()) {
                return AuthResult.Error("Password cannot be empty")
            }
            if (name.isBlank()) {
                return AuthResult.Error("Name cannot be empty")
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                return AuthResult.Error("Please enter a valid email address")
            }
            if (password.length < 6) {
                return AuthResult.Error("Password must be at least 6 characters long")
            }

            // Create user with Firebase
            val user = firebaseAuthDataSource.signUpWithEmail(email, password, name)
            val displayName = user.displayName ?: name

            AuthResult.Success(displayName)
        } catch (e: Exception) {
            // Parse Firebase error messages
            val errorMessage = when {
                e.message?.contains("already in use", ignoreCase = true) == true ->
                    "This email is already registered. Please sign in instead."

                e.message?.contains("weak password", ignoreCase = true) == true ->
                    "Password is too weak. Please use a stronger password."

                e.message?.contains("invalid email", ignoreCase = true) == true ->
                    "Invalid email format. Please check and try again."

                else -> e.message ?: "Sign up failed. Please try again."
            }

            AuthResult.Error(errorMessage)
        }
    }

    override suspend fun signInWithGoogle(idToken: String): AuthResult {
        return try {
            if (idToken.isBlank()) {
                return AuthResult.Error("Invalid Google token")
            }

            // Sign in with Google using Firebase
            val user = firebaseAuthDataSource.signInWithGoogle(idToken)
            val displayName = user.displayName ?: user.email ?: "User"

            AuthResult.Success(displayName)
        } catch (e: Exception) {
            AuthResult.Error(
                e.message ?: "Google Sign-In failed. Please try again."
            )
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): AuthResult {
        return try {
            // Validate email
            if (email.isBlank()) {
                return AuthResult.Error("Email cannot be empty")
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                return AuthResult.Error("Please enter a valid email address")
            }

            // Send password reset email
            firebaseAuthDataSource.sendPasswordResetEmail(email)

            // Return success (note: Firebase sends email even if account doesn't exist, for security)
            AuthResult.Success("Password reset email sent")
        } catch (e: Exception) {
            // Parse Firebase errors
            val errorMessage = when {
                e.message?.contains("no user record", ignoreCase = true) == true ->
                    "No account found with this email address."

                e.message?.contains("invalid email", ignoreCase = true) == true ->
                    "Invalid email format. Please check and try again."

                else -> e.message ?: "Failed to send reset email. Please try again."
            }

            AuthResult.Error(errorMessage)
        }
    }

    override fun signOut() {
        firebaseAuthDataSource.signOut()
    }

    // Additional helper methods (optional)

    /**
     * Get current user's email
     */
    fun getCurrentUserEmail(): String? {
        return firebaseAuthDataSource.currentUser?.email
    }

    /**
     * Get current user's display name
     */
    fun getCurrentUserDisplayName(): String? {
        return firebaseAuthDataSource.currentUser?.displayName
    }

    /**
     * Check if email is verified
     */
    fun isEmailVerified(): Boolean {
        return firebaseAuthDataSource.isEmailVerified()
    }

    /**
     * Send email verification
     */
    suspend fun sendEmailVerification(): AuthResult {
        return try {
            firebaseAuthDataSource.sendEmailVerification()
            AuthResult.Info("Verification email sent")
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Failed to send verification email")
        }
    }

    /**
     * Update display name
     */
    suspend fun updateDisplayName(newName: String): AuthResult {
        return try {
            if (newName.isBlank()) {
                return AuthResult.Error("Name cannot be empty")
            }

            firebaseAuthDataSource.updateDisplayName(newName)
            AuthResult.Success("Name updated")
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Failed to update name")
        }
    }

    /**
     * Update email
     */
    suspend fun updateEmail(newEmail: String): AuthResult {
        return try {
            if (newEmail.isBlank()) {
                return AuthResult.Error("Email cannot be empty")
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                return AuthResult.Error("Please enter a valid email address")
            }

            firebaseAuthDataSource.updateEmail(newEmail)
            AuthResult.Info("Email updated successfully")
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Failed to update email")
        }
    }

    /**
     * Update password
     */
    suspend fun updatePassword(newPassword: String): AuthResult {
        return try {
            if (newPassword.isBlank()) {
                return AuthResult.Error("Password cannot be empty")
            }
            if (newPassword.length < 6) {
                return AuthResult.Error("Password must be at least 6 characters long")
            }

            firebaseAuthDataSource.updatePassword(newPassword)
            AuthResult.Success("Password updated")
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Failed to update password")
        }
    }

    /**
     * Delete user account
     */
    suspend fun deleteAccount(): AuthResult {
        return try {
            firebaseAuthDataSource.deleteAccount()
            AuthResult.Success("Account deleted successfully")
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Failed to delete account")
        }
    }
}