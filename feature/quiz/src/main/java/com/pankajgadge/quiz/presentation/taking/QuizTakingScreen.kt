package com.pankajgadge.quiz.presentation.taking

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pankajgadge.core.domain.model.Question
import com.pankajgadge.core.domain.model.Quiz

/**
 * Quiz Taking Screen
 * Main screen for taking a quiz with timer and question navigation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizTakingScreen(
    quizId: String,
    onNavigateBack: () -> Unit,
    onQuizCompleted: (String) -> Unit, // Navigate to results with resultId
    viewModel: QuizTakingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val timeRemaining by viewModel.timeRemaining.collectAsState()
    var showExitDialog by remember { mutableStateOf(false) }
    var showSubmitDialog by remember { mutableStateOf(false) }

    // Load quiz on first composition
    LaunchedEffect(quizId) {
        viewModel.loadQuiz(quizId)
    }

    // Navigate to results when submitted
    LaunchedEffect(uiState) {
        if (uiState is QuizTakingUiState.Submitted) {
            val resultId = (uiState as QuizTakingUiState.Submitted).response.resultId
            onQuizCompleted(resultId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    when (val state = uiState) {
                        is QuizTakingUiState.InProgress -> Text(state.quiz.title)
                        else -> Text("Quiz")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { showExitDialog = true }) {
                        Icon(Icons.Default.Close, contentDescription = "Exit")
                    }
                },
                actions = {
                    // Timer
                    TimerChip(timeRemaining = timeRemaining)
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is QuizTakingUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is QuizTakingUiState.InProgress -> {
                    QuizQuestionContent(
                        question = state.currentQuestion,
                        questionNumber = state.questionNumber,
                        totalQuestions = state.totalQuestions,
                        selectedAnswer = state.selectedAnswer,
                        onAnswerSelected = viewModel::selectAnswer,
                        onNext = viewModel::nextQuestion,
                        onPrevious = viewModel::previousQuestion,
                        canGoBack = state.questionNumber > 1
                    )
                }

                is QuizTakingUiState.ReadyToSubmit -> {
                    SubmitConfirmationContent(
                        quiz = state.quiz,
                        answeredQuestions = state.answeredQuestions,
                        totalQuestions = state.totalQuestions,
                        onSubmit = { showSubmitDialog = true },
                        onGoBack = viewModel::previousQuestion
                    )
                }

                is QuizTakingUiState.Submitting -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Submitting your answers...")
                    }
                }

                is QuizTakingUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onNavigateBack) {
                            Text("Go Back")
                        }
                    }
                }

                else -> {}
            }
        }

        // Exit Confirmation Dialog
        if (showExitDialog) {
            AlertDialog(
                onDismissRequest = { showExitDialog = false },
                icon = { Icon(Icons.Default.Warning, contentDescription = null) },
                title = { Text("Exit Quiz?") },
                text = { Text("Your progress will be lost if you exit now. Are you sure?") },
                confirmButton = {
                    TextButton(onClick = onNavigateBack) {
                        Text("Exit")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showExitDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Submit Confirmation Dialog
        if (showSubmitDialog) {
            AlertDialog(
                onDismissRequest = { showSubmitDialog = false },
                icon = { Icon(Icons.Default.CheckCircle, contentDescription = null) },
                title = { Text("Submit Quiz?") },
                text = { Text("Are you ready to submit your quiz? You cannot change your answers after submission.") },
                confirmButton = {
                    Button(onClick = {
                        showSubmitDialog = false
                        viewModel.submitQuiz()
                    }) {
                        Text("Submit")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showSubmitDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
private fun TimerChip(timeRemaining: Long) {
    val minutes = timeRemaining / 60
    val seconds = timeRemaining % 60

    val color = when {
        timeRemaining > 300 -> MaterialTheme.colorScheme.primary
        timeRemaining > 60 -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.error
    }

    Surface(
        color = color,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.padding(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Timer,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = String.format("%02d:%02d", minutes, seconds),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun QuizQuestionContent(
    question: Question,
    questionNumber: Int,
    totalQuestions: Int,
    selectedAnswer: Int?,
    onAnswerSelected: (Int) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    canGoBack: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Progress Indicator
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Question $questionNumber of $totalQuestions",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "${question.points} pts",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = questionNumber.toFloat() / totalQuestions,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Question Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Text(
                text = question.text,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Answer Options
        question.options.forEachIndexed { index, option ->
            AnswerOption(
                text = option,
                isSelected = selectedAnswer == index,
                onClick = { onAnswerSelected(index) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Navigation Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = onPrevious,
                enabled = canGoBack
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Previous")
            }

            Button(
                onClick = onNext,
                enabled = selectedAnswer != null
            ) {
                Text(if (questionNumber == totalQuestions) "Review" else "Next")
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null)
            }
        }
    }
}

@Composable
private fun AnswerOption(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = isSelected,
                onClick = onClick
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.secondaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        border = if (isSelected) {
            CardDefaults.outlinedCardBorder()
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text.replace("\"", ""),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun SubmitConfirmationContent(
    quiz: Quiz,
    answeredQuestions: Int,
    totalQuestions: Int,
    onSubmit: () -> Unit,
    onGoBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.AssignmentTurnedIn,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Ready to Submit?",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = quiz.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Answered: $answeredQuestions / $totalQuestions questions",
                    style = MaterialTheme.typography.bodyLarge
                )
                if (answeredQuestions < totalQuestions) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Warning: ${totalQuestions - answeredQuestions} questions unanswered",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Submit Quiz")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onGoBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Review Answers")
        }
    }
}