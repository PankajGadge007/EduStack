package com.pankajgadge.core.domain.model

import java.util.Date

/**
 * Represents the result of a completed quiz
 * Used for quiz history and leaderboard
 */
data class QuizResult(
    val id: String = "",
    val userId: String,
    val quizId: String,
    val quizTitle: String,
    val score: Int,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val wrongAnswers: Int,
    val totalPoints: Int,
    val earnedPoints: Int,
    val timeTaken: Long, // in seconds
    val completedAt: Date,
    val answers: Map<String, QuestionAnswer> = emptyMap()
)

/**
 * Represents user's answer to a question
 */
data class QuestionAnswer(
    val questionId: String,
    val questionType: QuestionType,
    val userAnswer: String, // Can be option index, text, or paired items
    val correctAnswer: String,
    val isCorrect: Boolean,
    val pointsEarned: Int
)

/**
 * Types of questions supported
 */
enum class QuestionType {
    MULTIPLE_CHOICE,      // Current type
    FILL_IN_BLANK,        // Future
    PAIR_UP,              // Future
    TRUE_FALSE            // Future (optional)
}

/**
 * Summary statistics for user profile
 */
data class QuizStats(
    val totalQuizzesTaken: Int = 0,
    val totalScore: Int = 0,
    val averageScore: Double = 0.0,
    val highestScore: Int = 0,
    val totalTimePlayed: Long = 0, // in seconds
    val lastQuizDate: Date? = null
)