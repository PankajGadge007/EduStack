package com.pankajgadge.firebase.repository

import com.pankajgadge.api.repository.AuthRepository
import com.pankajgadge.common.auth.AuthResult
import com.pankajgadge.common.auth.UserInfo
import com.pankajgadge.firebase.datasource.FirebaseAuthDataSource

import javax.inject.Inject

class FirebaseAuthRepositoryImpl @Inject constructor(
    private val authDataSource: FirebaseAuthDataSource
) : AuthRepository {

    override suspend fun signIn(email: String, password: String): AuthResult {
        return authDataSource.signInWithEmail(email, password)
    }

    override suspend fun signUp(email: String, password: String, name: String): AuthResult {
        return authDataSource.signUpWithEmail(email, password, name)
    }

    override fun isLoggedIn(): Boolean {
        return authDataSource.isLoggedIn()
    }

    override fun getCurrentUser(): UserInfo? {
        return authDataSource.getCurrentUser()
    }

    override fun signOut() {
        authDataSource.signOut()
    }
}