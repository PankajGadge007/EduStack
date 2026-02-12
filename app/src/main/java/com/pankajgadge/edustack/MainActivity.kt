package com.pankajgadge.edustack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.pankajgadge.edustack.navigation.NavGraph
import com.pankajgadge.edustack.ui.theme.EduStackTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import javax.inject.Inject

/**
 * Main Activity - Production Ready
 * Entry point with proper lifecycle management and error handling
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            EduStackTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    EduStackApp(firebaseAuth)
                }
            }
        }
    }
}

/**
 * Main app composable
 * Handles authentication check and navigation setup
 */
@Composable
fun EduStackApp(firebaseAuth: FirebaseAuth) {
    val navController = rememberNavController()
    var appState by remember { mutableStateOf<AppState>(AppState.Loading) }
    val lifecycleOwner = LocalLifecycleOwner.current

    // Check authentication state on launch
    LaunchedEffect(Unit) {
        try {
            // Optional: Add minimum splash duration for smooth UX
            delay(300)

            val isLoggedIn = firebaseAuth.currentUser != null
            appState = AppState.Success(
                startDestination = if (isLoggedIn) "dashboard" else "login"
            )
        } catch (e: Exception) {
            appState = AppState.Error(e.message ?: "Failed to initialize app")
        }
    }

    // Listen to auth state changes
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                // Refresh auth state when app resumes
                val isLoggedIn = firebaseAuth.currentUser != null
                if (appState is AppState.Success) {
                    val currentDestination = (appState as AppState.Success).startDestination
                    val expectedDestination = if (isLoggedIn) "dashboard" else "login"

                    // If auth state changed, update start destination
                    if (currentDestination != expectedDestination) {
                        appState = AppState.Success(expectedDestination)
                    }
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Render based on app state
    when (val state = appState) {
        is AppState.Loading -> {
            LoadingScreen()
        }
        is AppState.Success -> {
            NavGraph(
                navController = navController,
                startDestination = state.startDestination
            )
        }
        is AppState.Error -> {
            ErrorScreen(
                message = state.message,
                onRetry = {
                    appState = AppState.Loading
                    // Retry initialization
                }
            )
        }
    }
}

/**
 * App state sealed class
 */
private sealed class AppState {
    object Loading : AppState()
    data class Success(val startDestination: String) : AppState()
    data class Error(val message: String) : AppState()
}

/**
 * Loading screen composable
 */
@Composable
private fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading EduStack...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Error screen composable
 */
@Composable
private fun ErrorScreen(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Error",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}