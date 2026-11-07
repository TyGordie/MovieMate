package com.example.moviemate.sync

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

/** WorkManager waits for connectivity and can retry even if the app is not in the foreground. */
object SyncScheduler {
    private const val UNIQUE_WORK_NAME = "moviemate_pending_action_sync"

    fun schedule(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val request = OneTimeWorkRequestBuilder<PendingSyncWorker>()
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(context.applicationContext).enqueueUniqueWork(
            UNIQUE_WORK_NAME,
            ExistingWorkPolicy.APPEND_OR_REPLACE,
            request
        )
    }
}
