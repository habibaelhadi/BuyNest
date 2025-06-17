package com.example.buynest.repos.authenticationrepo

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher

interface AuthenticationRepo {
    suspend fun loginWithEmailAndPassword(email: String, password: String): Result<Unit>
    suspend fun registerWithEmailAndPassword(name: String, phone: String,
                                             email: String, password: String): Result<Unit>
    suspend fun logInWithGoogle(context: Context, launcher: ActivityResultLauncher<Intent>): Result<Unit>
    suspend fun logout(): Result<Unit>
    fun getGoogleSignInIntent(context: Context): Intent?
    suspend fun sendResetPasswordEmail(email: String): Result<String>
}