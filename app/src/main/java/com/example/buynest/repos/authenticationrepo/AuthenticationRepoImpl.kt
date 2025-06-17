package com.example.buynest.repos.authenticationrepo

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.example.buynest.repos.authenticationrepo.firebase.Firebase
import com.example.buynest.repos.authenticationrepo.firebase.FirebaseResponse
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AuthenticationRepoImpl: AuthenticationRepo {
    private val firebase = Firebase.instance

    override suspend fun loginWithEmailAndPassword(
        email: String,
        password: String
    ): Result<Unit> = suspendCoroutine { continuation ->
        firebase.setFirebaseResponse(object : FirebaseResponse {
            override fun onResponseSuccess(message: String?) {
                continuation.resume(Result.success(Unit))
            }

            override fun onResponseFailure(message: String?) {
                continuation.resume(Result.failure(Exception(message)))
            }
        })
        firebase.login(email, password)
    }


    override suspend fun registerWithEmailAndPassword(
        name: String,
        phone: String,
        email: String,
        password: String
    ): Result<Unit> = suspendCoroutine { continuation ->
        firebase.setFirebaseResponse(object : FirebaseResponse {
            override fun onResponseSuccess(message: String?) {
                continuation.resume(Result.success(Unit))
            }

            override fun onResponseFailure(message: String?) {
                continuation.resume(Result.failure(Exception(message)))
            }
        })
        firebase.signup(name, phone, email, password)
    }

    override suspend fun logInWithGoogle(context: Context, launcher: ActivityResultLauncher<Intent>): Result<Unit> {
        val signInIntent = firebase.apply { connectToGoogle(context) }.getGoogleSignInIntent()
        return if (signInIntent != null) {
            launcher.launch(signInIntent)
            Result.success(Unit)
        } else {
            Result.failure(Exception("Missing dependencies"))
        }
    }

    override fun getGoogleSignInIntent(context: Context): Intent? {
        return firebase.apply { connectToGoogle(context) }.getGoogleSignInIntent()
    }

    override suspend fun sendResetPasswordEmail(email: String): Result<String> {
        return suspendCoroutine { continuation ->
            firebase.setFirebaseResponse(object : FirebaseResponse {
                override fun onResponseSuccess(message: String?) {
                    continuation.resume(Result.success("Password reset email sent."))
                }

                override fun onResponseFailure(message: String?) {
                    continuation.resume(Result.failure(Exception(message)))
                }
            })
            firebase.sendPasswordResetEmail(email)
        }
    }

    override suspend fun logout(): Result<Unit> {
        firebase.logout()
        var result: Result<Unit> = Result.failure(Exception("Login failed"))

        firebase.setFirebaseResponse(object : FirebaseResponse {
            override fun onResponseSuccess(message: String?) {
                result = Result.success(Unit)
            }

            override fun onResponseFailure(message: String?) {
                result = Result.failure(Exception(message))
            }

        })
        return result
    }
}