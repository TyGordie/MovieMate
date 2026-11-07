package com.example.moviemate.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.moviemate.R
import com.example.moviemate.model.Movie
import com.example.moviemate.model.Rating
import com.example.moviemate.repository.MovieRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun MovieDetailsScreen(movieId: String) {
    var movie by remember { mutableStateOf<Movie?>(null) }
    var message by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf("") }
    val repository = remember { MovieRepository() }
    val scope = rememberCoroutineScope()

    val unableLoad = stringResource(R.string.unable_load_movie)
    val addedMessage = stringResource(R.string.added_watchlist)
    val queuedMessage = stringResource(R.string.saved_for_sync)
    val unableAdd = stringResource(R.string.unable_add_movie)
    val enterRating = stringResource(R.string.enter_rating)
    val savedRating = stringResource(R.string.rating_saved)
    val queuedRating = stringResource(R.string.rating_saved_for_sync)
    val unableSaveRating = stringResource(R.string.unable_save_rating)

    LaunchedEffect(movieId) {
        try {
            movie = repository.getMovie(movieId)
            message = ""
        } catch (_: Exception) {
            message = unableLoad
        }
    }

    Column(Modifier.fillMaxSize().padding(24.dp)) {
        movie?.let { item ->
            Text(item.title, style = MaterialTheme.typography.headlineMedium)
            Text(stringResource(R.string.genre_value, item.genre), Modifier.padding(top = 12.dp))
            Text(stringResource(R.string.release_year_value, item.releaseYear), Modifier.padding(top = 8.dp))
            Text(item.description, Modifier.padding(top = 16.dp))
            Text(stringResource(R.string.rating_value, item.rating), Modifier.padding(top = 16.dp))

            Button(
                onClick = {
                    val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
                    if (userId.isBlank()) {
                        message = unableAdd
                    } else {
                        scope.launch {
                            val sent = repository.addToWatchlist(userId, item)
                            message = if (sent) addedMessage else queuedMessage
                        }
                    }
                },
                modifier = Modifier.padding(top = 20.dp)
            ) { Text(stringResource(R.string.add_watchlist)) }

            OutlinedTextField(
                value = rating,
                onValueChange = { rating = it },
                label = { Text(stringResource(R.string.rating_input)) },
                modifier = Modifier.padding(top = 16.dp),
                singleLine = true
            )
            Button(
                onClick = {
                    val value = rating.toDoubleOrNull()
                    if (value == null || value !in 1.0..5.0) {
                        message = enterRating
                    } else {
                        val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
                        if (userId.isBlank()) {
                            message = unableSaveRating
                        } else {
                            scope.launch {
                                val sent = repository.rateMovie(Rating(userId, item.id, value))
                                message = if (sent) savedRating else queuedRating
                            }
                        }
                    }
                },
                modifier = Modifier.padding(top = 10.dp)
            ) { Text(stringResource(R.string.rate_movie)) }
        }
        if (message.isNotEmpty()) Text(message, Modifier.padding(top = 16.dp))
    }
}
