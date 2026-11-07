package com.example.moviemate.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.moviemate.R
import com.example.moviemate.util.isValidEmail
import com.google.firebase.auth.FirebaseAuth

@Composable
fun RegisterScreen(onRegistered: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    val invalidEmail = stringResource(R.string.invalid_email)
    val shortPassword = stringResource(R.string.password_too_short)
    val mismatch = stringResource(R.string.password_mismatch)
    val registrationFailed = stringResource(R.string.registration_failed)

    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
        Text(stringResource(R.string.create_account), style = MaterialTheme.typography.headlineLarge)
        OutlinedTextField(
            value = email, onValueChange = { email = it },
            label = { Text(stringResource(R.string.email)) },
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp), singleLine = true
        )
        OutlinedTextField(
            value = password, onValueChange = { password = it },
            label = { Text(stringResource(R.string.password)) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp), singleLine = true
        )
        OutlinedTextField(
            value = confirmPassword, onValueChange = { confirmPassword = it },
            label = { Text(stringResource(R.string.confirm_password)) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp), singleLine = true
        )
        if (message.isNotEmpty()) Text(message, Modifier.padding(top = 12.dp))
        Button(
            onClick = {
                when {
                    !isValidEmail(email) -> message = invalidEmail
                    password.length < 6 -> message = shortPassword
                    password != confirmPassword -> message = mismatch
                    else -> {
                        loading = true
                        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.trim(), password)
                            .addOnSuccessListener { loading = false; onRegistered() }
                            .addOnFailureListener { error ->
                                Log.e("MovieMateAuth", "Registration failed", error)
                                loading = false
                                message = registrationFailed
                            }
                    }
                }
            },
            enabled = !loading,
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
        ) { Text(if (loading) stringResource(R.string.loading) else stringResource(R.string.register)) }
    }
}
