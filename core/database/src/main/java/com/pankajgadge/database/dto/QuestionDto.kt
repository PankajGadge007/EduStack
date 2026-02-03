package com.pankajgadge.database.dto

data class QuestionDto(
    val id: String,
    val text: String,
    val options: List<String>,
    val correctAnswer: Int,
    val points: Int
)