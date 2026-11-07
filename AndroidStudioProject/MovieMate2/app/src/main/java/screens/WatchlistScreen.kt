package com.example.moviemate.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.moviemate.R
import com.example.moviemate.model.Movie
import com.example.moviemate.repository.MovieRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun WatchlistScreen(onMovieSelected: (String) -> Unit) {
    var movies by remember { mutableStateOf<List<Movie>>(emptyList()) }
    val repository = remember { MovieRepository() }
    val scope = rememberCoroutineScope()
    val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    val removedMessage = stringResource(R.string.removed_watchlist)
    val queuedMessage = stringResource(R.string.saved_for_sync)
    var message by remember { mutableStateOf("") }

    LaunchedEffect(userId) {
        if (userId.isNotBlank()) movies = repository.getWatchlist(userId)
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text(stringResource(R.string.watchlist), style = MaterialTheme.typography.headlineMedium)
        if (movies.isEmpty()) Text(stringResource(R.string.no_watchlist), Modifier.padding(top = 16.dp))
        if (message.isNotEmpty()) Text(message, Modifier.padding(top = 8.dp))
        LazyColumn(Modifier.padding(top = 16.dp)) {
            items(movies, key = { it.id }) { movie ->
                Row(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                    Button(onClick = { onMovieSelected(movie.id) }, modifier = Modifier.weight(1f)) {
                        Text(movie.title)
                    }
                    Button(
                        onClick = {
                            scope.launch {
                                val sent = repository.removeFromWatchlist(userId, movie.id)
                                movies = repository.getWatchlist(userId)
                                message = if (sent) removedMessage else queuedMessage
                            }
                        },
                        modifier = Modifier.padding(start = 8.dp)
                    ) { Text(stringResource(R.string.remove)) }
                }
            }
        }
    }
}
