package com.example.moviemate.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.os.LocaleListCompat
import com.example.moviemate.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging

@Composable
fun SettingsScreen(
    onLogout: () -> Unit,
    onDarkModeChanged: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val preferences = remember {
        context.getSharedPreferences("MovieMateSettings", Context.MODE_PRIVATE)
    }
    var notifications by remember { mutableStateOf(preferences.getBoolean("notifications", false)) }
    var darkMode by remember { mutableStateOf(preferences.getBoolean("darkMode", false)) }
    var expanded by remember { mutableStateOf(false) }
    var permissionMessage by remember { mutableStateOf("") }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        notifications = granted
        preferences.edit().putBoolean("notifications", granted).apply()
        if (granted) {
            FirebaseMessaging.getInstance().subscribeToTopic("moviemate_updates")
        }
        permissionMessage = if (granted) "" else context.getString(R.string.notification_permission_denied)
    }

    val languageCode = AppCompatDelegate.getApplicationLocales().get(0)?.language ?: "en"
    val languageName = when (languageCode) {
        "xh" -> stringResource(R.string.xhosa)
        "zu" -> stringResource(R.string.zulu)
        else -> stringResource(R.string.english)
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(stringResource(R.string.settings), style = MaterialTheme.typography.headlineLarge)
        Text(stringResource(R.string.language))

        Box {
            Button(onClick = { expanded = true }) { Text(languageName) }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.english)) },
                    onClick = {
                        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("en"))
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.xhosa)) },
                    onClick = {
                        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("xh"))
                        expanded = false
                    }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.zulu)) },
                    onClick = {
                        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("zu"))
                        expanded = false
                    }
                )
            }
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(stringResource(R.string.notifications))
            Switch(
                checked = notifications,
                onCheckedChange = { enabled ->
                    if (enabled && Build.VERSION.SDK_INT >= 33 &&
                        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
                    ) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        notifications = enabled
                        preferences.edit().putBoolean("notifications", enabled).apply()
                        if (enabled) {
                            FirebaseMessaging.getInstance().subscribeToTopic("moviemate_updates")
                        } else {
                            FirebaseMessaging.getInstance().unsubscribeFromTopic("moviemate_updates")
                        }
                    }
                }
            )
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(stringResource(R.string.dark_mode))
            Switch(
                checked = darkMode,
                onCheckedChange = { enabled ->
                    darkMode = enabled
                    preferences.edit().putBoolean("darkMode", enabled).apply()
                    onDarkModeChanged(enabled)
                }
            )
        }

        Text(stringResource(R.string.preferences_saved))
        if (permissionMessage.isNotEmpty()) Text(permissionMessage)

        Button(onClick = {
            FirebaseAuth.getInstance().signOut()
            onLogout()
        }) { Text(stringResource(R.string.logout)) }
    }
}
