package com.example.moviemate.repository

import android.util.Log
import com.example.moviemate.MovieCache
import com.example.moviemate.PendingSyncAction
import com.example.moviemate.model.Movie
import com.example.moviemate.model.Rating
import com.example.moviemate.sync.SyncScheduler
import com.example.moviemate.network.RetrofitInstance

class MovieRepository {
    suspend fun getMovies(): List<Movie> = try {
        RetrofitInstance.api.getMovies().also(MovieCache::saveMovies)
    } catch (error: Exception) {
        Log.w(TAG, "Returning cached movies", error)
        MovieCache.getMovies()
    }

    suspend fun searchMovies(query: String): List<Movie> = try {
        RetrofitInstance.api.searchMovies(query).also(MovieCache::saveMovies)
    } catch (error: Exception) {
        Log.w(TAG, "Search unavailable; searching cached movies", error)
        MovieCache.searchMovies(query)
    }

    suspend fun getMovie(id: String): Movie = try {
        RetrofitInstance.api.getMovie(id).also { MovieCache.saveMovies(listOf(it)) }
    } catch (error: Exception) {
        Log.w(TAG, "Movie API unavailable; using cache", error)
        MovieCache.getMovie(id) ?: throw Exception("This movie has not been saved for offline use.")
    }

    suspend fun getWatchlist(userId: String): List<Movie> = try {
        RetrofitInstance.api.getWatchlist(userId).also { MovieCache.saveWatchlist(userId, it) }
    } catch (error: Exception) {
        Log.w(TAG, "Returning saved watchlist", error)
        MovieCache.getWatchlist(userId)
    }

    /** Returns true when the server accepted the action; false means it was saved for later sync. */
    suspend fun addToWatchlist(userId: String, movie: Movie): Boolean {
        MovieCache.addToWatchlist(userId, movie)
        return try {
            RetrofitInstance.api.addToWatchlist(userId, movie)
            true
        } catch (error: Exception) {
            Log.w(TAG, "Queueing watchlist addition for sync", error)
            MovieCache.enqueue(PendingSyncAction(type = ADD_WATCHLIST, userId = userId, movie = movie))
            SyncScheduler.schedule(MovieCache.applicationContext())
            false
        }
    }

    suspend fun removeFromWatchlist(userId: String, movieId: String): Boolean {
        MovieCache.removeFromWatchlist(userId, movieId)
        return try {
            RetrofitInstance.api.removeFromWatchlist(movieId, userId)
            true
        } catch (error: Exception) {
            Log.w(TAG, "Queueing watchlist removal for sync", error)
            MovieCache.enqueue(PendingSyncAction(type = REMOVE_WATCHLIST, userId = userId, movieId = movieId))
            SyncScheduler.schedule(MovieCache.applicationContext())
            false
        }
    }

    suspend fun rateMovie(rating: Rating): Boolean = try {
        RetrofitInstance.api.rateMovie(rating)
        true
    } catch (error: Exception) {
        Log.w(TAG, "Queueing rating for sync", error)
        MovieCache.enqueue(PendingSyncAction(type = SAVE_RATING, userId = rating.userId, rating = rating))
        SyncScheduler.schedule(MovieCache.applicationContext())
        false
    }

    /** Retries pending offline actions in order and removes each only after server success. */
    suspend fun syncPendingActions(): Int {
        var synced = 0
        for (action in MovieCache.getPendingActions()) {
            try {
                when (action.type) {
                    ADD_WATCHLIST -> {
                        val movie = action.movie
                        if (movie == null) {
                            MovieCache.removePendingAction(action.actionId)
                            continue
                        }
                        RetrofitInstance.api.addToWatchlist(action.userId, movie)
                    }
                    REMOVE_WATCHLIST -> {
                        val movieId = action.movieId
                        if (movieId.isNullOrBlank()) {
                            MovieCache.removePendingAction(action.actionId)
                            continue
                        }
                        RetrofitInstance.api.removeFromWatchlist(movieId, action.userId)
                    }
                    SAVE_RATING -> {
                        val rating = action.rating
                        if (rating == null) {
                            MovieCache.removePendingAction(action.actionId)
                            continue
                        }
                        RetrofitInstance.api.rateMovie(rating)
                    }
                    else -> {
                        Log.w(TAG, "Skipping unknown queued action: ${action.type}")
                        MovieCache.removePendingAction(action.actionId)
                        continue
                    }
                }
                MovieCache.removePendingAction(action.actionId)
                synced++
            } catch (error: Exception) {
                Log.w(TAG, "Sync stopped; remaining actions will retry later", error)
                break
            }
        }
        Log.i(TAG, "Synced $synced pending MovieMate action(s)")
        return synced
    }

    companion object {
        private const val TAG = "MovieMateRepository"
        private const val ADD_WATCHLIST = "ADD_WATCHLIST"
        private const val REMOVE_WATCHLIST = "REMOVE_WATCHLIST"
        private const val SAVE_RATING = "SAVE_RATING"
    }
}
