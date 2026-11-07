package com.example.moviemate

import android.content.Context
import android.content.SharedPreferences
import com.example.moviemate.model.Movie
import com.example.moviemate.model.Rating
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.UUID

/** Local cache and retry queue. Data is kept on this device until the API is reachable. */
data class PendingSyncAction(
    val actionId: String = UUID.randomUUID().toString(),
    val type: String = "",
    val userId: String = "",
    val movie: Movie? = null,
    val movieId: String? = null,
    val rating: Rating? = null
)

object MovieCache {
    private lateinit var preferences: SharedPreferences
    private lateinit var appContext: Context
    private val gson = Gson()
    private val lock = Any()

    fun initialize(context: Context) {
        synchronized(lock) {
            appContext = context.applicationContext
            preferences = appContext.getSharedPreferences(
                "MovieMateOffline", Context.MODE_PRIVATE
            )
        }
    }

    fun applicationContext(): Context {
        check(::appContext.isInitialized) { "MovieCache must be initialized in MainActivity." }
        return appContext
    }

    private fun prefs(): SharedPreferences {
        check(::preferences.isInitialized) { "MovieCache must be initialized in MainActivity." }
        return preferences
    }

    private fun readMovies(key: String): List<Movie> {
        val json = prefs().getString(key, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<Movie>>() {}.type
            gson.fromJson<List<Movie>>(json, type) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun saveList(key: String, movies: List<Movie>) {
        prefs().edit().putString(key, gson.toJson(movies)).apply()
    }

    fun saveMovies(movies: List<Movie>) {
        synchronized(lock) {
            val saved = readMovies("movies").associateBy { it.id }.toMutableMap()
            movies.forEach { saved[it.id] = it }
            saveList("movies", saved.values.toList())
        }
    }

    fun getMovies(): List<Movie> = synchronized(lock) { readMovies("movies") }

    fun getMovie(id: String): Movie? = getMovies().firstOrNull { it.id == id }

    fun searchMovies(query: String): List<Movie> = getMovies().filter {
        it.title.contains(query, ignoreCase = true) ||
            it.genre.contains(query, ignoreCase = true) ||
            it.description.contains(query, ignoreCase = true)
    }

    fun saveWatchlist(userId: String, movies: List<Movie>) {
        synchronized(lock) { saveList("watchlist_$userId", movies) }
    }

    fun getWatchlist(userId: String): List<Movie> = synchronized(lock) {
        readMovies("watchlist_$userId")
    }

    fun addToWatchlist(userId: String, movie: Movie) {
        synchronized(lock) {
            val movies = readMovies("watchlist_$userId").associateBy { it.id }.toMutableMap()
            movies[movie.id] = movie
            saveList("watchlist_$userId", movies.values.toList())
        }
    }

    fun removeFromWatchlist(userId: String, movieId: String) {
        synchronized(lock) {
            saveList("watchlist_$userId", readMovies("watchlist_$userId").filterNot { it.id == movieId })
        }
    }

    fun enqueue(action: PendingSyncAction) {
        synchronized(lock) {
            val actions = getPendingActions().toMutableList()
            actions.add(action)
            prefs().edit().putString("pending_actions", gson.toJson(actions)).apply()
        }
    }

    fun getPendingActions(): List<PendingSyncAction> = synchronized(lock) {
        val json = prefs().getString("pending_actions", null) ?: return@synchronized emptyList()
        try {
            val type = object : TypeToken<List<PendingSyncAction>>() {}.type
            gson.fromJson<List<PendingSyncAction>>(json, type) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun removePendingAction(actionId: String) {
        synchronized(lock) {
            val remaining = getPendingActions().filterNot { it.actionId == actionId }
            prefs().edit().putString("pending_actions", gson.toJson(remaining)).apply()
        }
    }
}
