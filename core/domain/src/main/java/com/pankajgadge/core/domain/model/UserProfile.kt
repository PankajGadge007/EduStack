package com.pankajgadge.core.domain.model

import java.util.Date

/**
 * Extended user profile with additional information
 * Combines Firebase Auth data with Firestore profile data
 */
data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole,
    val photoUrl: String? = null,
    val phone: String? = null,
    val bio: String? = null,
    val institution: String? = null,
    val grade: String? = null, // For students
    val subject: String? = null, // For teachers
    val joinedAt: Date,
    val stats: QuizStats = QuizStats(),
    val isEmailVerified: Boolean = false
)

/**
 * Convert User to UserProfile
 */
fun User.toUserProfile(
    photoUrl: String? = null,
    phone: String? = null,
    bio: String? = null,
    institution: String? = null,
    grade: String? = null,
    subject: String? = null,
    joinedAt: Date = Date(),
    stats: QuizStats = QuizStats(),
    isEmailVerified: Boolean = false
): UserProfile {
    return UserProfile(
        id = id,
        name = name,
        email = email,
        role = role,
        photoUrl = photoUrl,
        phone = phone,
        bio = bio,
        institution = institution,
        grade = grade,
        subject = subject,
        joinedAt = joinedAt,
        stats = stats,
        isEmailVerified = isEmailVerified
    )
}