package com.pankajgadge.user.presentation.profile

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pankajgadge.core.domain.model.UserProfile
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * User Profile Screen
 * Shows user information with edit capability
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToQuizHistory: () -> Unit,
    onSignOut: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isEditMode by viewModel.isEditMode.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Edit Profile" else "Profile") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    when (uiState) {
                        is ProfileUiState.Success -> {
                            if (isEditMode) {
                                TextButton(onClick = { viewModel.toggleEditMode() }) {
                                    Text("Cancel")
                                }
                                TextButton(onClick = { viewModel.saveProfile() }) {
                                    Text("Save")
                                }
                            } else {
                                IconButton(onClick = { viewModel.toggleEditMode() }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                                }
                            }
                        }

                        else -> {}
                    }
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
                is ProfileUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is ProfileUiState.Success -> {
                    ProfileContent(
                        profile = state.profile,
                        isEditMode = isEditMode,
                        onUpdateField = viewModel::updateField,
                        onNavigateToQuizHistory = onNavigateToQuizHistory,
                        onSignOut = {
                            viewModel.signOut()
                            onSignOut()
                        }
                    )
                }

                is ProfileUiState.Saving -> {
                    ProfileContent(
                        profile = state.profile,
                        isEditMode = isEditMode,
                        onUpdateField = viewModel::updateField,
                        onNavigateToQuizHistory = onNavigateToQuizHistory,
                        onSignOut = onSignOut,
                        isSaving = true
                    )
                }

                is ProfileUiState.Error -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
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
                        Button(onClick = { viewModel.loadProfile() }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileContent(
    profile: UserProfile,
    isEditMode: Boolean,
    onUpdateField: ((UserProfile) -> UserProfile) -> Unit,
    onNavigateToQuizHistory: () -> Unit,
    onSignOut: () -> Unit,
    isSaving: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Header
        ProfileHeader(profile = profile)

        Spacer(modifier = Modifier.height(24.dp))

        // Stats Cards
        if (!isEditMode) {
            StatsCards(profile = profile)
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Profile Information
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
                    text = "Personal Information",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Name Field
                ProfileField(
                    label = "Name",
                    value = profile.name,
                    isEditMode = isEditMode,
                    enabled = !isSaving,
                    onValueChange = { newName ->
                        onUpdateField { it.copy(name = newName) }
                    }
                )

                // Email Field (read-only)
                ProfileField(
                    label = "Email",
                    value = profile.email,
                    isEditMode = false,
                    leadingIcon = if (profile.isEmailVerified) {
                        Icons.Default.CheckCircle
                    } else null
                )

                // Phone Field
                ProfileField(
                    label = "Phone",
                    value = profile.phone ?: "",
                    isEditMode = isEditMode,
                    enabled = !isSaving,
                    onValueChange = { newPhone ->
                        onUpdateField { it.copy(phone = newPhone.ifBlank { null }) }
                    }
                )

                // Bio Field
                ProfileField(
                    label = "Bio",
                    value = profile.bio ?: "",
                    isEditMode = isEditMode,
                    enabled = !isSaving,
                    singleLine = false,
                    maxLines = 3,
                    onValueChange = { newBio ->
                        onUpdateField { it.copy(bio = newBio.ifBlank { null }) }
                    }
                )

                // Institution Field
                ProfileField(
                    label = "Institution",
                    value = profile.institution ?: "",
                    isEditMode = isEditMode,
                    enabled = !isSaving,
                    onValueChange = { newInstitution ->
                        onUpdateField { it.copy(institution = newInstitution.ifBlank { null }) }
                    }
                )

                // Role-specific fields
                when (profile.role) {
                    com.pankajgadge.core.domain.model.UserRole.STUDENT -> {
                        ProfileField(
                            label = "Grade",
                            value = profile.grade ?: "",
                            isEditMode = isEditMode,
                            enabled = !isSaving,
                            onValueChange = { newGrade ->
                                onUpdateField { it.copy(grade = newGrade.ifBlank { null }) }
                            }
                        )
                    }

                    com.pankajgadge.core.domain.model.UserRole.TEACHER -> {
                        ProfileField(
                            label = "Subject",
                            value = profile.subject ?: "",
                            isEditMode = isEditMode,
                            enabled = !isSaving,
                            onValueChange = { newSubject ->
                                onUpdateField { it.copy(subject = newSubject.ifBlank { null }) }
                            }
                        )
                    }

                    else -> {}
                }

                // Joined Date (read-only)
                val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                ProfileField(
                    label = "Member Since",
                    value = dateFormat.format(profile.joinedAt),
                    isEditMode = false
                )
            }
        }

        if (!isEditMode) {
            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            OutlinedButton(
                onClick = onNavigateToQuizHistory,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.History, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("View Quiz History")
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onSignOut,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Default.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sign Out")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ProfileHeader(profile: UserProfile) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Photo
        Surface(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = profile.name.firstOrNull()?.uppercase() ?: "U",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = profile.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = profile.role.name.lowercase().replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun StatsCards(profile: UserProfile) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatCard(
            title = "Quizzes",
            value = profile.stats.totalQuizzesTaken.toString(),
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "Avg Score",
            value = "${profile.stats.averageScore.toInt()}%",
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "Highest",
            value = "${profile.stats.highestScore}",
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun ProfileField(
    label: String,
    value: String,
    isEditMode: Boolean,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    onValueChange: (String) -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))

        if (isEditMode) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                enabled = enabled,
                singleLine = singleLine,
                maxLines = maxLines
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = value.ifBlank { "Not set" },
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (value.isBlank()) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
    }
}