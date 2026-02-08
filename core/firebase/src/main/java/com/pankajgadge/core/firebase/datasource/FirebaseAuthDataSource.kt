package com.pankajgadge.core.firebase.datasource

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data source for Firebase Authentication operations
 * Handles all direct Firebase Auth SDK calls
 */
@Singleton
class FirebaseAuthDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {

    /**
     * Get the currently signed-in Firebase user
     */
    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    /**
     * Check if a user is signed in
     */
    fun isUserSignedIn(): Boolean = firebaseAuth.currentUser != null

    /**
     * Sign in with email and password
     */
    suspend fun signInWithEmail(email: String, password: String): FirebaseUser {
        val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        return result.user ?: throw Exception("Sign in failed: User is null")
    }

    /**
     * Create new user with email and password
     */
    suspend fun signUpWithEmail(email: String, password: String, displayName: String): FirebaseUser {
        // Create the user
        val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user ?: throw Exception("Sign up failed: User is null")

        // Update the user's profile with display name
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(displayName)
            .build()

        user.updateProfile(profileUpdates).await()

        return user
    }

    /**
     * Sign in with Google
     * @param idToken Google ID token received from Google Sign-In
     * @return FirebaseUser if successful
     * @throws Exception if Google sign-in fails
     */
    suspend fun signInWithGoogle(idToken: String): FirebaseUser {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = firebaseAuth.signInWithCredential(credential).await()
        return result.user ?: throw Exception("Google Sign-In failed: User is null")
    }

    /**
     * Send password reset email
     * @param email User's email address
     */
    suspend fun sendPasswordResetEmail(email: String) {
        firebaseAuth.sendPasswordResetEmail(email).await()
    }

    /**
     * Sign out current user
     */
    fun signOut() {
        firebaseAuth.signOut()
    }

    /**
     * Update user's display name
     */
    suspend fun updateDisplayName(displayName: String) {
        val user = currentUser ?: throw Exception("No user signed in")
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(displayName)
            .build()
        user.updateProfile(profileUpdates).await()
    }

    /**
     * Update user's email
     */
    suspend fun updateEmail(newEmail: String) {
        val user = currentUser ?: throw Exception("No user signed in")
        user.updateEmail(newEmail).await()
    }

    /**
     * Update user's password
     */
    suspend fun updatePassword(newPassword: String) {
        val user = currentUser ?: throw Exception("No user signed in")
        user.updatePassword(newPassword).await()
    }

    /**
     * Send email verification
     */
    suspend fun sendEmailVerification() {
        val user = currentUser ?: throw Exception("No user signed in")
        user.sendEmailVerification().await()
    }

    /**
     * Check if email is verified
     */
    fun isEmailVerified(): Boolean = currentUser?.isEmailVerified ?: false

    /**
     * Reload user data
     */
    suspend fun reloadUser() {
        val user = currentUser ?: throw Exception("No user signed in")
        user.reload().await()
    }

    /**
     * Re-authenticate user (required before sensitive operations)
     * @param email User's email
     * @param password User's password
     * @throws Exception if re-authentication fails
     */
    suspend fun reauthenticate(email: String, password: String) {
        val user = currentUser ?: throw Exception("No user signed in")
        val credential = EmailAuthProvider.getCredential(email, password)
        user.reauthenticate(credential).await()
    }

    /**
     * Delete the current user account
     * @throws Exception if deletion fails
     */
    suspend fun deleteAccount() {
        val user = currentUser ?: throw Exception("No user signed in")
        user.delete().await()
    }
}