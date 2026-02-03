package com.pankajgadge.edustack.ui.screens.homescreen


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onQuizClick: () -> Unit,
    onPracticalClick: () -> Unit,
    onHelpClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("EduApp") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome to EduApp",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(vertical = 32.dp)
            )

            ModuleCard(
                title = "Quiz Tests",
                description = "Take quizzes and test your knowledge",
                icon = Icons.Default.Quiz,
                onClick = onQuizClick
            )

            ModuleCard(
                title = "IT Practicals",
                description = "Complete hands-on programming exercises",
                icon = Icons.Default.Code,
                onClick = onPracticalClick
            )

            ModuleCard(
                title = "App Help",
                description = "Get help and support",
                icon = Icons.Default.Help,
                onClick = onHelpClick
            )
        }
    }
}

@Composable
private fun ModuleCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}