package com.pankajgadge.core.database.dto

data class QuizDto(
    val id: String,
    val title: String,
    val description: String,
    val questions: List<QuestionDto>,
    val duration: Int,
    val createdBy: String,
    val difficulty: String,
    val createdAt: Long
)

