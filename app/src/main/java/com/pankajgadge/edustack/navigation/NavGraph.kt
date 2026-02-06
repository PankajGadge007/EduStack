package com.pankajgadge.edustack.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pankajgadge.edustack.ui.screens.dashboard.DashboardScreen
import com.pankajgadge.auth.presentation.login.LoginScreen
import com.pankajgadge.auth.presentation.login.LoginViewModel
import com.pankajgadge.quiz.presentation.detail.QuizDetailScreen
import com.pankajgadge.quiz.presentation.list.QuizListScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Dashboard : Screen("dashboard")
    object QuizList : Screen("quiz_list")
    object QuizDetail : Screen("quiz_detail/{quizId}") {
        fun createRoute(quizId: String) = "quiz_detail/$quizId"
    }

    object PracticalList : Screen("practical_list")
    object PracticalDetail : Screen("practical_detail/{practicalId}") {
        fun createRoute(practicalId: String) = "practical_detail/$practicalId"
    }

    object Help : Screen("help")
}

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    loginViewModel: LoginViewModel = hiltViewModel()
) {
    val isLoggedIn by loginViewModel.isLoggedIn.collectAsState()

    // Navigate to appropriate screen based on login status
    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            navController.navigate(Screen.Dashboard.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) Screen.Dashboard.route else Screen.Login.route
    ) {
        // Login Screen
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        // Dashboard/Home Screen
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onQuizClick = { navController.navigate(Screen.QuizList.route) },
                onPracticalClick = { navController.navigate(Screen.PracticalList.route) },
                onHelpClick = { navController.navigate(Screen.Help.route) },
                onLogout = {
                    loginViewModel.signOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Quiz List Screen
        composable(Screen.QuizList.route) {
            QuizListScreen(
                onQuizClick = { quizId ->
                    navController.navigate(Screen.QuizDetail.createRoute(quizId))
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        // Practical List Screen (Placeholder)
        composable(Screen.PracticalList.route) {
            PlaceholderScreen(
                title = "IT Practicals",
                message = "Coming Soon!",
                onBackClick = { navController.popBackStack() }
            )
        }

        // Help Screen (Placeholder)
        composable(Screen.Help.route) {
            PlaceholderScreen(
                title = "Help",
                message = "Help documentation coming soon!",
                onBackClick = { navController.popBackStack() }
            )
        }

        // Quiz Detail Screen
        composable(Screen.QuizDetail.route) { backStackEntry ->
            val quizId = backStackEntry.arguments?.getString("quizId") ?: return@composable
            QuizDetailScreen(
                quizId = quizId,
                onBackClick = { navController.popBackStack() }
            )
        }

        // Practical Detail Screen (Placeholder)
        composable(Screen.PracticalDetail.route) {
            PlaceholderScreen(
                title = "Practical Detail",
                message = "Coming Soon!",
                onBackClick = { navController.popBackStack() }
            )
        }

    }
}

// ============================================
// Placeholder Screen for unimplemented features
// ============================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaceholderScreen(
    title: String,
    message: String,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Text("←")
                    }
                }
            )
        }
    ) { paddingValues ->
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.headlineMedium
            )
        }
    }
}