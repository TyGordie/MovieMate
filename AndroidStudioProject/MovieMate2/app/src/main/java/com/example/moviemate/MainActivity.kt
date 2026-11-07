package com.example.moviemate

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.moviemate.navigation.MovieMateNavigation
import com.example.moviemate.notifications.MovieMateMessagingService
import com.example.moviemate.sync.SyncScheduler
import com.example.moviemate.ui.theme.MovieMateTheme
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MovieCache.initialize(applicationContext)
        SyncScheduler.schedule(applicationContext)
        MovieMateMessagingService.createNotificationChannel(applicationContext)
        val settings = getSharedPreferences("MovieMateSettings", MODE_PRIVATE)
        if (settings.getBoolean("notifications", false)) {
            FirebaseMessaging.getInstance().subscribeToTopic("moviemate_updates")
        }

        setContent {
            val preferences = remember {
                getSharedPreferences("MovieMateSettings", MODE_PRIVATE)
            }
            var darkMode by remember {
                mutableStateOf(preferences.getBoolean("darkMode", false))
            }

            MovieMateTheme(darkTheme = darkMode) {
                MovieMateNavigation(
                    onDarkModeChanged = { enabled -> darkMode = enabled }
                )
            }
        }
    }
}
