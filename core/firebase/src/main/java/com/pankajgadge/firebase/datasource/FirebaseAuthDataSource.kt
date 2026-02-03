package com.pankajgadge.firebase.datasource

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.pankajgadge.common.auth.AuthResult
import com.pankajgadge.common.auth.UserInfo
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthDataSource @Inject constructor(
    private val auth: FirebaseAuth
) {

    suspend fun signInWithEmail(email: String, password: String): AuthResult {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("User not found")

            AuthResult.Success(
                userId = user.uid,
                email = user.email ?: "",
                displayName = user.displayName ?: email.substringBefore("@")
            )
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Login failed")
        }
    }

    suspend fun signUpWithEmail(email: String, password: String, name: String): AuthResult {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("User creation failed")

            // Update profile with name
            val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
            user.updateProfile(profileUpdates).await()

            AuthResult.Success(
                userId = user.uid,
                email = user.email ?: "",
                displayName = name
            )
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Sign up failed")
        }
    }

    suspend fun signInWithGoogle(idToken: String): AuthResult {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val user = result.user ?: throw Exception("User not found")

            AuthResult.Success(
                userId = user.uid,
                email = user.email ?: "",
                displayName = user.displayName ?: ""
            )
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Google sign in failed")
        }
    }

    fun getCurrentUser(): UserInfo? {
        val user = auth.currentUser ?: return null
        return UserInfo(
            userId = user.uid,
            email = user.email ?: "",
            displayName = user.displayName ?: ""
        )
    }

    fun isLoggedIn(): Boolean = auth.currentUser != null

    fun signOut() {
        auth.signOut()
    }
}