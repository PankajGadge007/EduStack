package com.pankajgadge.core.domain.auth

/**
 * Sealed class representing authentication operation results
 * Used across all authentication features
 */
sealed class AuthResult {
    /**
     * Successful authentication operation
     * @param displayName User's display name to show in UI
     */
    data class Success(val displayName: String) : AuthResult()

    /**
     * Failed authentication operation
     * @param message Error message to display to user
     */
    data class Error(val message: String) : AuthResult()

    /**
     * Informational message (non-error)
     * @param info Information message to display to user
     */
    data class Info(val info: String) : AuthResult()
}