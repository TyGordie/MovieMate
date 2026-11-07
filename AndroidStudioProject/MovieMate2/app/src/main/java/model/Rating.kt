package com.example.moviemate.model

data class Rating(
    val userId: String = "",
    val movieId: String = "",
    val rating: Double = 0.0
)