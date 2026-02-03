package com.pankajgadge.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey val id: String,
    val quizId: String,
    val text: String,
    val options: String, // JSON string
    val correctAnswer: Int,
    val points: Int
)