package com.pankajgadge.domain.model

data class Practical(
    val id: String,
    val title: String,
    val description: String,
    val steps: List<PracticalStep>,
    val difficulty: Difficulty,
    val estimatedTime: Int
)

data class PracticalStep(
    val stepNumber: Int,
    val instruction: String,
    val codeSnippet: String?,
    val isCompleted: Boolean = false
)

enum class Difficulty {
    EASY, MEDIUM, HARD
}