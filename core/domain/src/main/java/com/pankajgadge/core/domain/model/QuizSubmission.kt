package com.pankajgadge.core.domain.model

import java.util.Date

/**
 * Represents an in-progress quiz submission
 * Used while user is taking a quiz
 */
data class QuizSubmission(
    val quizId: String,
    val userId: String,
    val startedAt: Date,
    val answers: MutableMap<String, SubmittedAnswer> = mutableMapOf(),
    val currentQuestionIndex: Int = 0,
    val timeRemaining: Long = 0 // in seconds
)

/**
 * Represents user's answer to a single question
 */
data class SubmittedAnswer(
    val questionId: String,
    val selectedAnswer: String, // For MCQ: option index, For fill-in-blank: text
    val answeredAt: Date = Date()
)

/**
 * Request to submit a quiz
 */
data class SubmitQuizRequest(
    val quizId: String,
    val userId: String,
    val answers: Map<String, SubmittedAnswer>,
    val timeTaken: Long, // in seconds
    val startedAt: Date,
    val completedAt: Date
)

/**
 * Response after submitting a quiz
 */
data class SubmitQuizResponse(
    val resultId: String,
    val score: Int,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val wrongAnswers: Int,
    val totalPoints: Int,
    val earnedPoints: Int,
    val timeTaken: Long,
    val quizResult: QuizResult
)