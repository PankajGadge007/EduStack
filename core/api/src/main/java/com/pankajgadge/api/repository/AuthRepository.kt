package com.pankajgadge.api.repository

import com.pankajgadge.common.auth.AuthResult
import com.pankajgadge.common.auth.UserInfo

interface AuthRepository {
    suspend fun signIn(email: String, password: String): AuthResult
    suspend fun signUp(email: String, password: String, name: String): AuthResult
    fun isLoggedIn(): Boolean
    fun getCurrentUser(): UserInfo?
    fun signOut()
}