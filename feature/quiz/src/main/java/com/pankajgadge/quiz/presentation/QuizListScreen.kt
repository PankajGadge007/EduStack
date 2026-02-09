package com.pankajgadge.quiz.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pankajgadge.core.common.result.Result
import com.pankajgadge.core.domain.model.Quiz
import com.pankajgadge.quiz.model.QuizViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizListScreen(
    onQuizClick: (String) -> Unit,
    onBackClick: () -> Unit,
    viewModel: QuizViewModel = hiltViewModel()
) {
    val quizzesState by viewModel.quizzesState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quizzes") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Text("←")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (val state = quizzesState) {
                is Result.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is Result.Success -> {
                    if (state.data.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No quizzes available")
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.data) { quiz ->
                                QuizCard(
                                    quiz = quiz,
                                    onClick = { onQuizClick(quiz.id) }
                                )
                            }
                        }
                    }
                }

                is Result.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = state.exception.message ?: "Unknown error",
                                color = MaterialTheme.colorScheme.error
                            )
                            Button(onClick = { viewModel.loadQuizzes() }) {
                                Text("Retry")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuizCard(
    quiz: Quiz,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
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