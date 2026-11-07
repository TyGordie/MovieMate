package com.example.moviemate.sync

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.moviemate.MovieCache
import com.example.moviemate.repository.MovieRepository

/** Retries pending watchlist and rating actions whenever a connected network is available. */
class PendingSyncWorker(
    appContext: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(appContext, workerParameters) {

    override suspend fun doWork(): Result {
        return try {
            MovieCache.initialize(applicationContext)
            MovieRepository().syncPendingActions()
            if (MovieCache.getPendingActions().isEmpty()) {
                Result.success()
            } else {
                Log.w(TAG, "Some MovieMate actions remain queued; WorkManager will retry")
                Result.retry()
            }
        } catch (error: Exception) {
            Log.e(TAG, "MovieMate background sync failed", error)
            Result.retry()
        }
    }

    companion object {
        private const val TAG = "MovieMateSyncWorker"
    }
}
