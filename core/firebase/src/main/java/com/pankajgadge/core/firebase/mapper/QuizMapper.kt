package com.pankajgadge.core.firebase.mapper

import com.pankajgadge.core.domain.model.Question
import com.pankajgadge.core.domain.model.Quiz
import com.pankajgadge.core.firebase.datasource.FirebaseQuestion
import com.pankajgadge.core.firebase.datasource.FirebaseQuiz
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