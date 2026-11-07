package com.example.moviemate.util

fun isValidEmail(email: String): Boolean =
    Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$").matches(email.trim())

fun isValidRating(rating: Double): Boolean =
    rating.isFinite() && rating in 1.0..5.0
