package com.example.naijadishes.ui


import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.naijadishes.model.Recipe
import com.example.naijadishes.ui.screen.HomeScreen
import com.example.naijadishes.ui.screen.LoginScreen
import com.example.naijadishes.ui.screen.RecipeScreen
import com.example.naijadishes.ui.screen.RegisterScreen
import com.example.naijadishes.ui.screen.SearchScreen
import com.example.naijadishes.ui.screen.UploadScreen
import com.example.naijadishes.ui.screen.UserProfileScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Recipe: Screen("recipe")
    object Search: Screen("search")
    object UserProfile: Screen("user_profile")
    object Upload: Screen("upload")
}

@Composable
fun AppNavHost(navController: NavHostController) {
    var recipe: Recipe? by remember { mutableStateOf(null) }
    var user by remember{mutableStateOf("")}
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) {
                            inclusive = true
                        }
                    }
                },
                onRegisterClick = { navController.navigate(Screen.Register.route) },
                appViewModel = viewModel(factory = AppViewModelProvider.Factory)
            )
        }
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccessful = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) {
                            inclusive = true
                        }
                    }
                },
                appViewModel = viewModel(factory = AppViewModelProvider.Factory)
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                appViewModel = viewModel(factory = AppViewModelProvider.Factory),
                backToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) {
                            inclusive = true
                        }
                    }
                },
                onClickRecipe = { recipe = it; navController.navigate(Screen.Recipe.route) },
                onCreateRecipe = {navController.navigate(Screen.Upload.route)},
                onSearchClicked = { navController.navigate(Screen.Search.route) },
            )
        }
        composable(Screen.Recipe.route) {
            RecipeScreen(
                onBackClick = { navController.popBackStack() },
                recipe = recipe,
                onAuthorClicked = {user = it; navController.navigate(Screen.UserProfile.route)},
            )
        }
        composable(Screen.Search.route) {
            SearchScreen(
                onBackClicked = { navController.popBackStack() },
                appViewModel = viewModel(factory = AppViewModelProvider.Factory),
                backToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) {
                            inclusive = true
                        }
                    }
                },
                onClickRecipe = { recipe = it; navController.navigate(Screen.Recipe.route) }
            )
        }
        composable(Screen.UserProfile.route){
            UserProfileScreen(
                username = user,
                appViewModel = viewModel(factory = AppViewModelProvider.Factory),
                backToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(Screen.Upload.route){
            UploadScreen(
                appViewModel = viewModel(factory = AppViewModelProvider.Factory),
                backToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}

