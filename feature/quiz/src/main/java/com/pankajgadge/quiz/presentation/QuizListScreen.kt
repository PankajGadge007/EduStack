package com.pankajgadge.quiz.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pankajgadge.domain.model.Quiz
import com.pankajgadge.ui.components.ErrorMessage
import com.pankajgadge.ui.components.LoadingIndicator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizListScreen(
    onQuizClick: (String) -> Unit,
    viewModel: QuizListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quizzes") }
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is QuizListUiState.Loading -> {
                LoadingIndicator(Modifier.padding(paddingValues))
            }
            is QuizListUiState.Success -> {
                QuizList(
                    quizzes = state.quizzes,
                    onQuizClick = onQuizClick,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            is QuizListUiState.Error -> {
                ErrorMessage(
                    message = state.message,
                    onRetry = { viewModel.loadQuizzes() },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun QuizList(
    quizzes: List<Quiz>,
    onQuizClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(quizzes) { quiz ->
            QuizCard(
                quiz = quiz,
                onClick = { onQuizClick(quiz.id) }
            )
        }
    }
}

@Composable
private fun QuizCard(
    quiz: Quiz,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = quiz.title,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = quiz.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${quiz.questions.size} Questions",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "${quiz.duration} min",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}