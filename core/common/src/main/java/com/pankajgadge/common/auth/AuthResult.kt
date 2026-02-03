package com.pankajgadge.common.auth

sealed class AuthResult {
    data class Success(
        val userId: String,
        val email: String,
        val displayName: String
    ) : AuthResult()

    data class Error(val message: String) : AuthResult()
}

data class UserInfo(
    val userId: String,
    val email: String,
    val displayName: String
)