package com.pankajgadge.auth.data.repository


import com.pankajgadge.common.auth.AuthResult
import com.pankajgadge.domain.model.User
import com.pankajgadge.domain.repository.AuthRepository
import com.pankajgadge.common.result.Result
import com.pankajgadge.common.result.errorResult
import com.pankajgadge.common.result.successResult
import com.pankajgadge.domain.model.UserRole
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
    private val mockUsers = mutableMapOf<String, MockUserData>()
    private var currentUserId: String? = null

    data class MockUserData(
        val email: String,
        val password: String,
        val name: String,
        val role: UserRole
    )

    override suspend fun signIn(
        email: String,
        password: String
    ): AuthResult {
        TODO("implemented using fire base Firebase Auth")
    }

    override suspend fun signUp(
        email: String,
        password: String,
        name: String
    ): AuthResult {
        TODO("implemented using fire base Firebase Auth")
    }

    suspend fun login(email: String, password: String): Result<User> {
        delay(1000)

        val mockUser = mockUsers[email]

        return if (mockUser != null && mockUser.password == password) {
            currentUserId = email
            successResult(
                User(
                    id = email.hashCode().toString(),
                    email = email,
                    name = mockUser.name,
                    role = mockUser.role
                )
            )
        } else if (email == "test@example.com" && password == "password123") {
            currentUserId = email
            successResult(
                User(
                    id = "test_user_123",
                    email = email,
                    name = "Test User",
                    role = UserRole.STUDENT
                )
            )
        } else {
            errorResult("Invalid email or password")
        }
    }

    suspend fun register(
        name: String,
        email: String,
        password: String,
        role: UserRole
    ): Result<User> {
        delay(1000)

        if (email.isEmpty() || !email.contains("@")) {
            return errorResult("Invalid email address")
        }

        if (password.length < 6) {
            return errorResult("Password must be at least 6 characters")
        }

        if (name.isEmpty()) {
            return errorResult("Name cannot be empty")
        }

        if (mockUsers.containsKey(email)) {
            return errorResult("Email already registered")
        }

        mockUsers[email] = MockUserData(email, password, name, role)
        currentUserId = email

        return successResult(
            User(
                id = email.hashCode().toString(),
                email = email,
                name = name,
                role = role
            )
        )
    }

    suspend fun logout(): Result<Unit> {
        delay(500)
        currentUserId = null
        return successResult(Unit)
    }

    override fun isLoggedIn(): Boolean {
        TODO("Not yet implemented")
    }

//    fun getCurrentUser(): UserInfo? {
//        TODO("Not yet implemented")
//    }

//    override suspend fun getCurrentUser(): Result<User?> {
//        delay(300)
//
//        val userId = currentUserId
//        if (userId == null) {
//            return successResult(null)
//        }
//
//        val mockUser = mockUsers[userId]
//        return if (mockUser != null) {
//            successResult(
//                User(
//                    id = userId.hashCode().toString(),
//                    email = mockUser.email,
//                    name = mockUser.name,
//                    role = mockUser.role
//                )
//            )
//        } else {
//            successResult(null)
//        }
//    }

    override fun signOut() {
        TODO("Not yet implemented")
    }



    // ✅ Forgot Password Implementation
    override suspend fun sendPasswordResetEmail(email: String): AuthResult {
        delay(1500)

//        return when {
//            email.isEmpty() -> errorResult("Email cannot be empty")
//            !email.contains("@") -> errorResult("Invalid email format")
//            else -> {
//                // Simulate successful email sent
//                successResult(Unit)
//            }
//        }
        return TODO("Provide the return value")
    }

    suspend fun verifyResetCode(code: String): Result<Boolean> {
        delay(1000)
        // Fake implementation - codes starting with "RESET" are valid
        return successResult(code.startsWith("RESET"))
    }

    suspend fun resetPasswordWithCode(code: String, newPassword: String): Result<Unit> {
        delay(1500)

        return when {
            newPassword.length < 6 -> errorResult("Password must be at least 6 characters")
            !code.startsWith("RESET") -> errorResult("Invalid or expired reset code")
            else -> successResult(Unit)
        }
    }

    // ✅ Google Sign-In Implementation
    override suspend fun signInWithGoogle(idToken: String): AuthResult {
        delay(1500)

//        return if (idToken.isNotEmpty()) {
//            currentUserId = "google_user_${System.currentTimeMillis()}"
//            successResult(
//                User(
//                    id = currentUserId!!,
//                    email = "google.user@example.com",
//                    name = "Google User",
//                    role = UserRole.STUDENT
//                )
//            )
//        } else {
//            errorResult("Invalid Google token")
//        }
        return TODO("Provide the return value")
    }
}