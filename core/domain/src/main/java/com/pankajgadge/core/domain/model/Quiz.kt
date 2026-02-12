package com.pankajgadge.core.domain.model

data class Quiz(
    val id: String,
    val title: String,
    val description: String,
    val questions: List<Question>,
    val duration: Int, // in minutes
    val createdBy: String,
    val createdAt: Long,
    val difficulty: String
)