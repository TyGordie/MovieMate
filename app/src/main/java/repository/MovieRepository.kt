package com.example.moviemate.repository

import com.example.moviemate.model.Movie
import com.example.moviemate.model.Rating
import com.example.moviemate.network.RetrofitInstance

class MovieRepository {

    suspend fun getMovies(): List<Movie> {
        return RetrofitInstance.api.getMovies()
    }

    suspend fun searchMovies(query: String): List<Movie> {
        return RetrofitInstance.api.searchMovies(query)
    }

    suspend fun getMovie(id: String): Movie {
        return RetrofitInstance.api.getMovie(id)
    }

    suspend fun getWatchlist(userId: String): List<Movie> {
        return RetrofitInstance.api.getWatchlist(userId)
    }

    suspend fun addToWatchlist(userId: String, movie: Movie) {
        RetrofitInstance.api.addToWatchlist(userId, movie)
    }

    suspend fun removeFromWatchlist(userId: String, movieId: String) {
        RetrofitInstance.api.removeFromWatchlist(movieId, userId)
    }

    suspend fun rateMovie(rating: Rating) {
        RetrofitInstance.api.rateMovie(rating)
    }
}