package com.pankajgadge.firebase.datasource

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseQuizDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val quizzesCollection = firestore.collection("quizzes")
    private val submissionsCollection = firestore.collection("submissions")

    suspend fun getQuizzes(): List<FirebaseQuiz> {
        return try {
            val snapshot = quizzesCollection
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                try {
                    val questionsData = doc.get("questions") as? List<Map<String, Any>>
                        ?: return@mapNotNull null

                    FirebaseQuiz(
                        id = doc.id,
                        title = doc.getString("title") ?: return@mapNotNull null,
                        description = doc.getString("description") ?: "",
                        questions = questionsData.mapNotNull { q ->
                            try {
                                FirebaseQuestion(
                                    id = q["id"] as? String ?: return@mapNotNull null,
                                    text = q["text"] as? String ?: return@mapNotNull null,
                                    options = (q["options"] as? List<String>) ?: return@mapNotNull null,
                                    correctAnswer = (q["correctAnswer"] as? Long)?.toInt() ?: return@mapNotNull null,
                                    points = (q["points"] as? Long)?.toInt() ?: 10,
                                    imageUrl = q["imageUrl"] as? String
                                )
                            } catch (e: Exception) {
                                null
                            }
                        },
                        duration = doc.getLong("duration")?.toInt() ?: 30,
                        createdBy = doc.getString("createdBy") ?: "",
                        createdAt = doc.getTimestamp("createdAt")?.toDate()?.time ?: 0L
                    )
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            throw Exception("Failed to fetch quizzes: ${e.message}")
        }
    }

    suspend fun getQuizById(quizId: String): FirebaseQuiz {
        return try {
            val doc = quizzesCollection.document(quizId).get().await()

            val questionsData = doc.get("questions") as? List<Map<String, Any>>
                ?: throw Exception("Invalid quiz data")

            FirebaseQuiz(
                id = doc.id,
                title = doc.getString("title") ?: throw Exception("Missing title"),
                description = doc.getString("description") ?: "",
                questions = questionsData.mapNotNull { q ->
                    try {
                        FirebaseQuestion(
                            id = q["id"] as? String ?: return@mapNotNull null,
                            text = q["text"] as? String ?: return@mapNotNull null,
                            options = (q["options"] as? List<String>) ?: return@mapNotNull null,
                            correctAnswer = (q["correctAnswer"] as? Long)?.toInt() ?: return@mapNotNull null,
                            points = (q["points"] as? Long)?.toInt() ?: 10,
                            imageUrl = q["imageUrl"] as? String
                        )
                    } catch (e: Exception) {
                        null
                    }
                },
                duration = doc.getLong("duration")?.toInt() ?: 30,
                createdBy = doc.getString("createdBy") ?: "",
                createdAt = doc.getTimestamp("createdAt")?.toDate()?.time ?: 0L
            )
        } catch (e: Exception) {
            throw Exception("Failed to fetch quiz: ${e.message}")
        }
    }

    suspend fun saveSubmission(
        userId: String,
        quizId: String,
        score: Int,
        totalPoints: Int,
        answers: Map<String, Int>
    ) {
        try {
            val submission = hashMapOf(
                "userId" to userId,
                "quizId" to quizId,
                "score" to score,
                "totalPoints" to totalPoints,
                "answers" to answers,
                "submittedAt" to com.google.firebase.Timestamp.now()
            )

            submissionsCollection.add(submission).await()
        } catch (e: Exception) {
            throw Exception("Failed to save submission: ${e.message}")
        }
    }
}

// Firebase DTOs
data class FirebaseQuiz(
    val id: String,
    val title: String,
    val description: String,
    val questions: List<FirebaseQuestion>,
    val duration: Int,
    val createdBy: String,
    val createdAt: Long
)

data class FirebaseQuestion(
    val id: String,
    val text: String,
    val options: List<String>,
    val correctAnswer: Int,
    val points: Int,
    val imageUrl: String? = null
)