package com.pankajgadge.core.firebase.auth

import com.google.firebase.auth.FirebaseAuth
import com.pankajgadge.core.domain.repository.AuthSessionRepository
import javax.inject.Inject

class FirebaseAuthSessionRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthSessionRepository {

    override fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }
}