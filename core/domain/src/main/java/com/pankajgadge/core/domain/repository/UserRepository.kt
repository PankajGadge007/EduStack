package com.pankajgadge.core.domain.repository

import com.pankajgadge.core.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun getCurrentUser(): Flow<User?>
    suspend fun logout()
}