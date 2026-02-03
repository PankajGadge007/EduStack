package com.pankajgadge.firebase.mapper

import com.pankajgadge.domain.model.Question
import com.pankajgadge.domain.model.Quiz
import com.pankajgadge.firebase.datasource.FirebaseQuestion
import com.pankajgadge.firebase.datasource.FirebaseQuiz
import javax.inject.Inject


class QuizMapper @Inject constructor() {

    fun toDomain(firebase: FirebaseQuiz): Quiz {
        return Quiz(
            id = firebase.id,
            title = firebase.title,
            description = firebase.description,
            questions = firebase.questions.map { toDomain(it) },
            duration = firebase.duration,
            createdBy = firebase.createdBy,
            createdAt = firebase.createdAt
        )
    }

    fun toDomain(firebase: FirebaseQuestion): Question {
        return Question(
            id = firebase.id,
            text = firebase.text,
            options = firebase.options,
            correctAnswer = firebase.correctAnswer,
            points = firebase.points,
            imageUrl = firebase.imageUrl
        )
    }
}