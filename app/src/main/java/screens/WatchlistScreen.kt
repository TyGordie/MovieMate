package com.example.moviemate.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.moviemate.model.Movie
import com.example.moviemate.repository.MovieRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun WatchlistScreen(
    onMovieSelected: (String) -> Unit
) {
    var movies by remember { mutableStateOf<List<Movie>>(emptyList()) }

    val repository = remember { MovieRepository() }
    val scope = rememberCoroutineScope()

    val userId =
        FirebaseAuth.getInstance().currentUser?.uid ?: ""

    LaunchedEffect(Unit) {
        try {
            movies = repository.getWatchlist(userId)
        } catch (e: Exception) {
            movies = emptyList()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "My Watchlist",
            style = MaterialTheme.typography.headlineMedium
        )

        LazyColumn(
            modifier = Modifier.padding(top = 16.dp)
        ) {
            items(movies) { movie ->

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {

                    Button(
                        onClick = {
                            onMovieSelected(movie.id)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(movie.title)
                    }

                    Button(
                        onClick = {
                            scope.launch {
                                repository.removeFromWatchlist(
                                    userId,
                                    movie.id
                                )

                                movies = repository.getWatchlist(userId)
                            }
                        },
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text("Remove")
                    }
                }
            }
        }
    }
}