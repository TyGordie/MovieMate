package com.example.moviemate.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.moviemate.model.Movie
import com.example.moviemate.model.Rating
import com.example.moviemate.repository.MovieRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun MovieDetailsScreen(
    movieId: String
) {
    var movie by remember { mutableStateOf<Movie?>(null) }
    var message by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf("") }

    val repository = remember { MovieRepository() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(movieId) {
        try {
            movie = repository.getMovie(movieId)
        } catch (e: Exception) {
            message = "Unable to load movie."
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        movie?.let {
            Text(
                text = it.title,
                style = MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "Genre: ${it.genre}",
                modifier = Modifier.padding(top = 12.dp)
            )

            Text(
                text = "Release year: ${it.releaseYear}",
                modifier = Modifier.padding(top = 8.dp)
            )

            Text(
                text = it.description,
                modifier = Modifier.padding(top = 16.dp)
            )

            Text(
                text = "Rating: ${it.rating}",
                modifier = Modifier.padding(top = 16.dp)
            )

            Button(
                onClick = {
                    val userId =
                        FirebaseAuth.getInstance().currentUser?.uid ?: ""

                    scope.launch {
                        try {
                            repository.addToWatchlist(userId, it)
                            message = "Added to watchlist."
                        } catch (e: Exception) {
                            message = "Unable to add movie."
                        }
                    }
                },
                modifier = Modifier.padding(top = 20.dp)
            ) {
                Text("Add to Watchlist")
            }

            OutlinedTextField(
                value = rating,
                onValueChange = { rating = it },
                label = { Text("Rating 1-5") },
                modifier = Modifier.padding(top = 16.dp)
            )

            Button(
                onClick = {
                    val value = rating.toDoubleOrNull()

                    if (value == null || value < 1 || value > 5) {
                        message = "Enter a rating from 1 to 5."
                    } else {
                        val userId =
                            FirebaseAuth.getInstance().currentUser?.uid ?: ""

                        scope.launch {
                            try {
                                repository.rateMovie(
                                    Rating(
                                        userId = userId,
                                        movieId = it.id,
                                        rating = value
                                    )
                                )

                                message = "Rating saved."
                            } catch (e: Exception) {
                                message = "Unable to save rating."
                            }
                        }
                    }
                },
                modifier = Modifier.padding(top = 10.dp)
            ) {
                Text("Rate Movie")
            }
        }

        if (message.isNotEmpty()) {
            Text(
                text = message,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}