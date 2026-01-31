package com.pankajgadge.quiz.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pankajgadge.domain.model.Question
import com.pankajgadge.ui.components.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizDetailScreen(
    onBackClick: () -> Unit,
    viewModel: QuizDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val answers by viewModel.answers.collectAsState()

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
        when (val state = uiState) {
            is QuizDetailUiState.Loading -> {
                LoadingIndicator(Modifier.padding(paddingValues))
            }
            is QuizDetailUiState.Success -> {
                QuizContent(
                    quiz = state.quiz,
                    answers = answers,
                    onAnswerSelected = viewModel::selectAnswer,
                    onSubmit = viewModel::submitQuiz,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            is QuizDetailUiState.Submitting -> {
                LoadingIndicator(Modifier.padding(paddingValues))
            }
            is QuizDetailUiState.Submitted -> {
                ResultScreen(
                    score = state.score,
                    onBackClick = onBackClick,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            is QuizDetailUiState.Error -> {
                ErrorMessage(
                    message = state.message,
                    onRetry = { /* reload */ },
                    modifier = Modifier.padding(paddingValues)
                )
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
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
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
            enabled = answers.size == quiz.questions.size
        ) {
            Text("Submit Quiz")
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
                        .padding(vertical = 8.dp)
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

@Composable
private fun ResultScreen(
    score: Int,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Quiz Completed!",
            style = MaterialTheme.typography.headlineLarge
        )
        Text(
            text = "Your Score: $score",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 16.dp)
        )
        Button(
            onClick = onBackClick,
            modifier = Modifier.padding(top = 32.dp)
        ) {
            Text("Back to Quizzes")
        }
    }
}