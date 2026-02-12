package com.pankajgadge.edustack.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.pankajgadge.auth.dashboard.DashboardScreen
import com.pankajgadge.auth.presentation.forgotpassword.ForgotPasswordScreen
import com.pankajgadge.edustack.ui.screens.login.LoginScreen
import com.pankajgadge.quiz.presentation.QuizListScreen
import com.pankajgadge.quiz.presentation.result.QuizResultScreen
import com.pankajgadge.quiz.presentation.taking.QuizTakingScreen
import com.pankajgadge.user.presentation.history.QuizHistoryScreen
import com.pankajgadge.user.presentation.profile.ProfileScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Login Screen
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToForgotPassword = {
                    navController.navigate("forgot_password")
                }
            )
        }

        // Forgot Password Screen
        composable("forgot_password") {
            ForgotPasswordScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Dashboard Screen
        composable("dashboard") {
            DashboardScreen(
                onNavigateToProfile = {
                    navController.navigate("profile")
                },
                onNavigateToQuizHistory = {
                    navController.navigate("quiz_history")
                },
                onNavigateToQuizList = {
                    navController.navigate("quiz_list")
                }
            )
        }

        // Profile Screen
        composable("profile") {
            ProfileScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToQuizHistory = {
                    navController.navigate("quiz_history")
                },
                onSignOut = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // Quiz History Screen
        composable("quiz_history") {
            QuizHistoryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onQuizClick = { resultId ->
                    navController.navigate("quiz_result/$resultId")
                }
            )
        }

        // Quiz List Screen
        composable("quiz_list") {
            QuizListScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onQuizClick = { quizId ->
                    // ✅ Navigate to Quiz Taking
                    navController.navigate("quiz_taking/$quizId")
        }
            )
        }

        // ✅ NEW: Quiz Taking Screen
        composable(
            route = "quiz_taking/{quizId}",
            arguments = listOf(
                navArgument("quizId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val quizId = backStackEntry.arguments?.getString("quizId") ?: return@composable

            QuizTakingScreen(
                quizId = quizId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onQuizCompleted = { resultId ->
                    // Navigate to results, removing quiz taking from back stack
                    navController.navigate("quiz_result/$resultId") {
                        popUpTo("quiz_taking/$quizId") { inclusive = true }
                    }
                }
            )
        }

        // ✅ NEW: Quiz Result Screen
        composable(
            route = "quiz_result/{resultId}",
            arguments = listOf(
                navArgument("resultId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val resultId = backStackEntry.arguments?.getString("resultId") ?: return@composable

            QuizResultScreen(
                resultId = resultId,
                onNavigateBack = {
                    navController.popBackStack("dashboard", inclusive = false)
                },
                onViewHistory = {
                    navController.navigate("quiz_history") {
                        popUpTo("dashboard")
                    }
                },
                onRetakeQuiz = { quizId ->
                    navController.navigate("quiz_taking/$quizId") {
                        popUpTo("quiz_result/$resultId") { inclusive = true }
                    }
                }
            )
        }
    }
}