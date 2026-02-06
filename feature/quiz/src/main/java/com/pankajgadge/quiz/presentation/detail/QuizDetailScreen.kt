package com.pankajgadge.quiz.presentation.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pankajgadge.common.result.Result
import com.pankajgadge.domain.model.Question
import com.pankajgadge.quiz.presentation.model.QuizViewModel
import com.pankajgadge.quiz.presentation.result.QuizResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizDetailScreen(
    quizId: String,
    onBackClick: () -> Unit,
    viewModel: QuizViewModel = hiltViewModel()
) {
    val selectedQuiz by viewModel.selectedQuiz.collectAsState()
    val answers by viewModel.answers.collectAsState()
    val submitState by viewModel.submitState.collectAsState()

    LaunchedEffect(quizId) {
        viewModel.loadQuiz(quizId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quiz") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Text("←")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (val state = selectedQuiz) {
                is Result.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is Result.Success -> {
                    when (val submit = submitState) {
                        is Result.Success -> {
                            QuizResult(
                                score = submit.data,
                                onBackClick = onBackClick
                            )
                        }
                        else -> {
                            QuizContent(
                                quiz = state.data,
                                answers = answers,
                                onAnswerSelected = viewModel::selectAnswer,
                                onSubmit = { viewModel.submitQuiz(quizId) },
                                isSubmitting = submit is Result.Loading
                            )
                        }
                    }
                }
                is Result.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.exception.message ?: "Failed to load quiz",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QuizContent(
    quiz: com.pankajgadge.domain.model.Quiz,
    answers: Map<String, Int>,
    onAnswerSelected: (String, Int) -> Unit,
    onSubmit: () -> Unit,
    isSubmitting: Boolean
) {
    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = quiz.title,
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = quiz.description,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            itemsIndexed(quiz.questions) { index, question ->
                QuestionItem(
                    questionNumber = index + 1,
                    question = question,
                    selectedAnswer = answers[question.id],
                    onAnswerSelected = { answerIndex ->
                        onAnswerSelected(question.id, answerIndex)
                    }
                )
            }
        }

        Button(
            onClick = onSubmit,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            enabled = answers.size == quiz.questions.size && !isSubmitting
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text("Submit Quiz")
            }
        }
    }
}

@Composable
private fun QuestionItem(
    questionNumber: Int,
    question: Question,
    selectedAnswer: Int?,
    onAnswerSelected: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Question $questionNumber",
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                text = question.text,
                style = MaterialTheme.typography.bodyLarge
            )

            question.options.forEachIndexed { index, option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = selectedAnswer == index,
                            onClick = { onAnswerSelected(index) }
                        )
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedAnswer == index,
                        onClick = { onAnswerSelected(index) }
                    )
                    Text(
                        text = option,
                        modifier = Modifier.padding(start = 8.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

