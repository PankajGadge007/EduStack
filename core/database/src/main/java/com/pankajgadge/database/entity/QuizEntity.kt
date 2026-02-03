package com.pankajgadge.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quizzes")
data class QuizEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val duration: Int,
    val createdBy: String,
    val createdAt: Long
)

