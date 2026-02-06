package com.pankajgadge.auth.data.repository

import com.pankajgadge.common.auth.AuthResult
import com.pankajgadge.common.auth.UserInfo
import com.pankajgadge.domain.model.User

import com.pankajgadge.domain.repository.AuthRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

/**
 * Fake implementation of AuthRepository for testing or development.
 * Replace with real Retrofit implementation when backend is ready.
 *
 * This provides mock authentication without needing an actual backend.
 */
class FakeAuthRepository @Inject constructor() : AuthRepository {

    // Simulate in-memory user storage
//    private val mockUsers = mutableMapOf<String, MockUserData>()
//    private var currentUserId: String? = null
//
//    data class MockUserData(
//        val email: String,
//        val password: String,
//        val name: String,
//        val role: String
//    )
//
//    override suspend fun login(email: String, password: String): Result<User> {
//        // Simulate network delay
//        delay(1000)
//
//        // Check if user exists and password matches
//        val mockUser = mockUsers[email]
//
//        return if (mockUser != null && mockUser.password == password) {
//            currentUserId = email
//            Result.Success(
//                User(
//                    id = email.hashCode().toString(),
//                    email = email,
//                    name = mockUser.name,
//                    role = mockUser.role
//                )
//            )
//        } else if (email == "test@example.com" && password == "password123") {
//            // Default test account
//            currentUserId = email
//            Result.Success(
//                User(
//                    id = "test_user_123",
//                    email = email,
//                    name = "Test User",
//                    role = "student"
//                )
//            )
//        } else {
//            Result.Error("Invalid email or password")
//        }
//    }
//
//    override suspend fun register(
//        name: String,
//        email: String,
//        password: String,
//        role: String
//    ): Result<User> {
//        delay(1000)
//
//        // Validation
//        if (email.isEmpty() || !email.contains("@")) {
//            return Result.Error("Invalid email address")
//        }
//
//        if (password.length < 6) {
//            return Result.Error("Password must be at least 6 characters")
//        }
//
//        if (name.isEmpty()) {
//            return Result.Error("Name cannot be empty")
//        }
//
//        // Check if user already exists
//        if (mockUsers.containsKey(email)) {
//            return Result.Error("Email already registered")
//        }
//
//        // Register new user
//        mockUsers[email] = MockUserData(email, password, name, role)
//        currentUserId = email
//
//        return Result.Success(
//            User(
//                id = email.hashCode().toString(),
//                email = email,
//                name = name,
//                role = role
//            )
//        )
//    }
//
//    override suspend fun logout(): Result<Unit> {
//        delay(500)
//        currentUserId = null
//        return Result.Success(Unit)
//    }
//
//    override suspend fun getCurrentUser(): Result<User?> {
//        delay(300)
//
//        val userId = currentUserId
//        if (userId == null) {
//            return Result.Success(null)
//        }
//
//        val mockUser = mockUsers[userId]
//        return if (mockUser != null) {
//            Result.Success(
//                User(
//                    id = userId.hashCode().toString(),
//                    email = mockUser.email,
//                    name = mockUser.name,
//                    role = mockUser.role
//                )
//            )
//        } else {
//            Result.Success(null)
//        }
//    }
//
//    override suspend fun forgotPassword(email: String): Result<Unit> {
//        delay(1000)
//
//        return if (email.isEmpty() || !email.contains("@")) {
//            Result.Error("Invalid email address")
//        } else if (mockUsers.containsKey(email)) {
//            // Simulate sending password reset email
//            Result.Success(Unit)
//        } else {
//            // For security, don't reveal if email exists
//            Result.Success(Unit)
//        }
//    }
    override suspend fun signIn(
        email: String,
        password: String
    ): AuthResult {
        TODO("Not yet implemented")
    }

    override suspend fun signUp(
        email: String,
        password: String,
        name: String
    ): AuthResult {
        TODO("Not yet implemented")
    }

    override fun isLoggedIn(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getCurrentUser(): UserInfo {
        TODO("Not yet implemented")
    }

    override fun signOut() {
        TODO("Not yet implemented")
    }
}