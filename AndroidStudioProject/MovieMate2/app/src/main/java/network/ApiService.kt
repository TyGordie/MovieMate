package com.example.moviemate.network

import com.example.moviemate.model.Movie
import com.example.moviemate.model.Rating
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("movies")
    suspend fun getMovies(): List<Movie>

    @GET("movies")
    suspend fun searchMovies(
        @Query("query") query: String
    ): List<Movie>

    @GET("movies/{id}")
    suspend fun getMovie(
        @Path("id") id: String
    ): Movie

    @GET("api/watchlist")
    suspend fun getWatchlist(
        @Query("userId") userId: String
    ): List<Movie>

    @POST("api/watchlist")
    suspend fun addToWatchlist(
        @Query("userId") userId: String,
        @Body movie: Movie
    )

    @DELETE("api/watchlist/{movieId}")
    suspend fun removeFromWatchlist(
        @Path("movieId") movieId: String,
        @Query("userId") userId: String
    )

    @POST("api/ratings")
    suspend fun rateMovie(
        @Body rating: Rating
    )
}