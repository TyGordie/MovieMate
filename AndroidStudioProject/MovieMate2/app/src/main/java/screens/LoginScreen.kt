package com.example.moviemate.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.example.moviemate.R
import com.example.moviemate.util.isValidEmail
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(onLogin: () -> Unit, onRegister: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var googleLoading by remember { mutableStateOf(false) }
    var loginLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val activity = context.findActivity()
    val credentialManager = remember { CredentialManager.create(context) }
    val scope = rememberCoroutineScope()
    val invalidEmail = stringResource(R.string.invalid_email)
    val enterPassword = stringResource(R.string.enter_password)
    val loginFailed = stringResource(R.string.login_failed)
    val googleFailed = stringResource(R.string.google_sign_in_failed)

    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
        Text("MovieMate", style = MaterialTheme.typography.headlineLarge)
        Text(stringResource(R.string.sign_in_continue), Modifier.padding(bottom = 24.dp))
        OutlinedTextField(
            value = email, onValueChange = { email = it },
            label = { Text(stringResource(R.string.email)) },
            modifier = Modifier.fillMaxWidth(), singleLine = true
        )
        OutlinedTextField(
            value = password, onValueChange = { password = it },
            label = { Text(stringResource(R.string.password)) },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp), singleLine = true
        )
        if (message.isNotEmpty()) Text(message, Modifier.padding(top = 12.dp))
        Button(
            onClick = {
                when {
                    !isValidEmail(email) -> message = invalidEmail
                    password.isBlank() -> message = enterPassword
                    else -> {
                        loginLoading = true
                        FirebaseAuth.getInstance().signInWithEmailAndPassword(email.trim(), password)
                            .addOnSuccessListener { loginLoading = false; onLogin() }
                            .addOnFailureListener { error ->
                                Log.e("MovieMateAuth", "Email login failed", error)
                                loginLoading = false
                                message = loginFailed
                            }
                    }
                }
            },
            enabled = !loginLoading && !googleLoading,
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
        ) { Text(if (loginLoading) stringResource(R.string.loading) else stringResource(R.string.login)) }
        Button(
            onClick = {
                message = ""
                if (activity == null) {
                    message = googleFailed
                } else {
                    googleLoading = true
                    scope.launch {
                        try {
                            val option = GetGoogleIdOption.Builder()
                                .setFilterByAuthorizedAccounts(false)
                                .setServerClientId(context.getString(R.string.default_web_client_id))
                                .setAutoSelectEnabled(false)
                                .build()
                            val request = GetCredentialRequest.Builder().addCredentialOption(option).build()
                            val result = credentialManager.getCredential(activity, request)
                            val credential = result.credential
                            if (credential is CustomCredential &&
                                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
                            ) {
                                val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
                                val firebaseCredential = GoogleAuthProvider.getCredential(googleCredential.idToken, null)
                                FirebaseAuth.getInstance().signInWithCredential(firebaseCredential)
                                    .addOnSuccessListener { googleLoading = false; onLogin() }
                                    .addOnFailureListener { error ->
                                        Log.e("MovieMateAuth", "Google Firebase sign-in failed", error)
                                        googleLoading = false
                                        message = googleFailed
                                    }
                            } else {
                                googleLoading = false
                                message = googleFailed
                            }
                        } catch (error: GetCredentialException) {
                            Log.e("MovieMateAuth", "Google credential provider unavailable", error)
                            googleLoading = false
                            message = context.getString(R.string.google_provider_unavailable)
                        } catch (error: Exception) {
                            Log.e("MovieMateAuth", "Google sign-in failed", error)
                            googleLoading = false
                            message = googleFailed
                        }
                    }
                }
            },
            enabled = !googleLoading && !loginLoading,
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
        ) { Text(if (googleLoading) stringResource(R.string.signing_in) else stringResource(R.string.continue_google)) }
        Button(onClick = onRegister, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Text(stringResource(R.string.create_account))
        }
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
