package com.example.moviemate.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.moviemate.screens.HomeScreen
import com.example.moviemate.screens.LoginScreen
import com.example.moviemate.screens.MovieDetailsScreen
import com.example.moviemate.screens.RegisterScreen
import com.example.moviemate.screens.SearchScreen
import com.example.moviemate.screens.SettingsScreen
import com.example.moviemate.screens.WatchlistScreen

@Composable
fun MovieMateNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {

        composable("login") {
            LoginScreen(
                onLogin = {
                    navController.navigate("home") {
                        popUpTo("login") {
                            inclusive = true
                        }
                    }
                },
                onRegister = {
                    navController.navigate("register")
                }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegistered = {
                    navController.navigate("home") {
                        popUpTo("login") {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable("home") {
            HomeScreen(
                onSearch = {
                    navController.navigate("search")
                },
                onWatchlist = {
                    navController.navigate("watchlist")
                },
                onSettings = {
                    navController.navigate("settings")
                }
            )
        }

        composable("search") {
            SearchScreen(
                onMovieSelected = { movieId ->
                    navController.navigate("details/$movieId")
                }
            )
        }

        composable("details/{movieId}") { backStackEntry ->
            val movieId =
                backStackEntry.arguments?.getString("movieId") ?: ""

            MovieDetailsScreen(
                movieId = movieId
            )
        }

        composable("watchlist") {
            WatchlistScreen(
                onMovieSelected = { movieId ->
                    navController.navigate("details/$movieId")
                }
            )
        }

        composable("settings") {
            SettingsScreen(
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(0)
                    }
                }
            )
        }
    }
}