package com.example.moviemate.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.moviemate.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/** Receives Firebase Cloud Messaging updates and displays them when notifications are enabled. */
class MovieMateMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Do not print the full device token to logs; retrieve it securely when registering a device.
        Log.i(TAG, "Firebase messaging token refreshed")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val preferences = getSharedPreferences("MovieMateSettings", Context.MODE_PRIVATE)
        if (!preferences.getBoolean("notifications", false)) return

        val title = message.notification?.title
            ?: message.data["title"]
            ?: getString(R.string.app_name)
        val body = message.notification?.body
            ?: message.data["body"]
            ?: getString(R.string.notification_default_message)

        createNotificationChannel(this)

        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w(TAG, "Notification received but permission is not granted")
            return
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(this).notify(System.currentTimeMillis().toInt(), notification)
        } catch (error: SecurityException) {
            Log.e(TAG, "Unable to display notification", error)
        }
    }


    companion object {
        private const val TAG = "MovieMateFCM"
        const val CHANNEL_ID = "moviemate_updates"

        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    context.getString(R.string.notification_channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = context.getString(R.string.notification_channel_description)
                }
                context.getSystemService(NotificationManager::class.java)
                    .createNotificationChannel(channel)
            }
        }
    }
}
