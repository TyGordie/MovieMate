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
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(onMovieSelected: (String) -> Unit) {
    var query by remember { mutableStateOf("") }
    var movies by remember { mutableStateOf<List<Movie>>(emptyList()) }
    var message by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val repository = remember { MovieRepository() }
    val emptyQuery = stringResource(R.string.please_enter_movie)
    val noMovies = stringResource(R.string.offline_no_movies)

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text(stringResource(R.string.search_movies), style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text(stringResource(R.string.movie_name)) },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            singleLine = true
        )
        Button(
            onClick = {
                if (query.isBlank()) {
                    message = emptyQuery
                } else {
                    scope.launch {
                        loading = true
                        movies = repository.searchMovies(query.trim())
                        message = if (movies.isEmpty()) noMovies else ""
                        loading = false
                    }
                }
            },
            enabled = !loading,
            modifier = Modifier.padding(top = 12.dp)
        ) { Text(if (loading) stringResource(R.string.loading) else stringResource(R.string.search)) }
        if (message.isNotEmpty()) Text(message, Modifier.padding(top = 12.dp))
        LazyColumn(Modifier.padding(top = 12.dp)) {
            items(movies, key = { it.id }) { movie ->
                Button(
                    onClick = { onMovieSelected(movie.id) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) { Text(movie.title) }
            }
        }
    }
}
