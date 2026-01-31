package com.pankajgadge.edustack.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.pankajgadge.edustack.ui.screens.homescreen.HomeScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
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
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onQuizClick = { navController.navigate(Screen.QuizList.route) },
                onPracticalClick = { navController.navigate(Screen.PracticalList.route) },
                onHelpClick = { navController.navigate(Screen.Help.route) }
            )
        }

        composable(Screen.QuizList.route) {
            QuizListScreen(
                onQuizClick = { quizId ->
                    navController.navigate(Screen.QuizDetail.createRoute(quizId))
                }
            )
        }

        composable(Screen.QuizDetail.route) {
            QuizDetailScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.PracticalList.route) {
            PracticalListScreen(
                onPracticalClick = { practicalId ->
                    navController.navigate(Screen.PracticalDetail.createRoute(practicalId))
                }
            )
        }

        composable(Screen.PracticalDetail.route) {
            PracticalDetailScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.Help.route) {
            HelpScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}