package com.example.moviemate.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.moviemate.R

@Composable
fun HomeScreen(onSearch: () -> Unit, onWatchlist: () -> Unit, onSettings: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(stringResource(R.string.welcome), style = MaterialTheme.typography.headlineLarge)
        Button(onClick = onSearch, modifier = Modifier.fillMaxWidth().padding(top = 20.dp)) {
            Text(stringResource(R.string.search_movies))
        }
        Button(onClick = onWatchlist, modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {
            Text(stringResource(R.string.watchlist))
        }
        Button(onClick = onSettings, modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {
            Text(stringResource(R.string.settings))
        }
    }
}
