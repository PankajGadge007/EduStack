package com.pankajgadge.core.domain.repository

import com.pankajgadge.core.common.auth.AuthResult

/**
 * Authentication repository interface
 * Defines the contract for authentication operations
 */
interface AuthRepository {

    /**
     * Check if a user is currently logged in
     * @return true if user is logged in, false otherwise
     */
    fun isLoggedIn(): Boolean

    /**
     * Sign in with email and password
     * @param email User's email address
     * @param password User's password
     * @return AuthResult.Success with display name if successful, AuthResult.Error otherwise
     */
    suspend fun signIn(email: String, password: String): AuthResult

    /**
     * Sign up with email, password, and name
     * @param email User's email address
     * @param password User's password
     * @param name User's display name
     * @return AuthResult.Success with display name if successful, AuthResult.Error otherwise
     */
    suspend fun signUp(email: String, password: String, name: String): AuthResult

    /**
     * Sign out the current user
     */
    fun signOut()

    /**
     * Send password reset email
     * @param email User's email address
     * @return AuthResult.Success if email sent successfully, AuthResult.Error otherwise
     */
    suspend fun sendPasswordResetEmail(email: String): AuthResult

    /**
     * Sign in with Google
     * @param idToken Google ID token received from Google Sign-In
     * @return AuthResult.Success with display name if successful, AuthResult.Error otherwise
     */
    suspend fun signInWithGoogle(idToken: String): AuthResult
}