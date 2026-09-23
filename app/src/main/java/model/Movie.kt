package com.example.moviemate.model

data class Movie(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val genre: String = "",
    val releaseYear: Int = 0,
    val rating: Double = 0.0,
    val poster: String = ""
)