package com.pankajgadge.domain.repository

import com.pankajgadge.common.result.Result
import com.pankajgadge.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun getCurrentUser(): Flow<User?>
    suspend fun logout()
}