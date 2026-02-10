package com.pankajgadge.core.domain.repository

interface AuthSessionRepository {
    fun getCurrentUserId(): String?
    fun signOut()
}
