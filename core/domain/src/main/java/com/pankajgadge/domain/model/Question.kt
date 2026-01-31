package com.pankajgadge.domain.model

data class Question(
    val id: String,
    val text: String,
    val options: List<String>,
    val correctAnswer: Int,
    val points: Int,
    val imageUrl: String? = null  // ← Add this field for future image support
)
