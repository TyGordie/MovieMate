package com.example.moviemate.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable

@Composable
fun HomeScreen(
    onSearch: () -> Unit,
    onWatchlist: () -> Unit,
    onSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome to MovieMate")

        Button(
            onClick = onSearch,
            modifier = Modifier.padding(top = 20.dp)
        ) {
            Text("Search Movies")
        }

        Button(
            onClick = onWatchlist,
            modifier = Modifier.padding(top = 10.dp)
        ) {
            Text("My Watchlist")
        }

        Button(
            onClick = onSettings,
            modifier = Modifier.padding(top = 10.dp)
        ) {
            Text("Settings")
        }
    }
}