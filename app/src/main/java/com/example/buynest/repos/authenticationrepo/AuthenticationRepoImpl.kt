package com.example.buynest.repos.authenticationrepo

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.example.buynest.repos.authenticationrepo.firebase.Firebase
import com.example.buynest.repos.authenticationrepo.firebase.FirebaseResponse

class AuthenticationRepoImpl: AuthenticationRepo {
    private val firebase = Firebase.instance

    override suspend fun loginWithEmailAndPassword(email: String, password: String): Result<Unit> {
        firebase.login(email,password)
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

    override suspend fun registerWithEmailAndPassword(
        name: String,
        phone: String,
        email: String,
        password: String
    ): Result<Unit> {
        firebase.signup(name, phone, email, password)
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

    override suspend fun logInWithGoogle(context: Context, launcher: ActivityResultLauncher<Intent>): Result<Unit> {
        val signInIntent = firebase.apply { connectToGoogle(context) }.getGoogleSignInIntent()
        return if (signInIntent != null) {
            launcher.launch(signInIntent)
            Result.success(Unit)
        } else {
            Result.failure(Exception("Missing dependencies"))
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