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
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(
    onMovieSelected: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var movies by remember { mutableStateOf<List<Movie>>(emptyList()) }
    var message by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val repository = remember { MovieRepository() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Search Movies",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Movie name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )

        Button(
            onClick = {
                if (query.isBlank()) {
                    message = "Please enter a movie name."
                } else {
                    scope.launch {
                        try {
                            movies = repository.searchMovies(query)
                            message = ""
                        } catch (e: Exception) {
                            message = "Unable to load movies."
                        }
                    }
                }
            },
            modifier = Modifier.padding(top = 12.dp)
        ) {
            Text("Search")
        }

        if (message.isNotEmpty()) {
            Text(
                text = message,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        LazyColumn(
            modifier = Modifier.padding(top = 12.dp)
        ) {
            items(movies) { movie ->
                Button(
                    onClick = { onMovieSelected(movie.id) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(movie.title)
                }
            }
        }
    }
}