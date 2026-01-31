package com.pankajgadge.database


import androidx.room.Database
import androidx.room.RoomDatabase
import com.pankajgadge.database.dao.QuizDao
import com.pankajgadge.database.entity.QuestionEntity
import com.pankajgadge.database.entity.QuizEntity


@Database(
    entities = [QuizEntity::class, QuestionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class EduStackDatabase : RoomDatabase() {
    abstract fun quizDao(): QuizDao
}