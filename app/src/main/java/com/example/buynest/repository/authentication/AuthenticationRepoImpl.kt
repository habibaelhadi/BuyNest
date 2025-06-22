package com.example.buynest.repository.authentication

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.example.buynest.model.state.FirebaseResponse
import com.example.buynest.repository.authentication.firebase.FirebaseRepository
import com.example.buynest.repository.authentication.shopify.ShopifyAuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AuthenticationRepoImpl(
    private val firebaseRepository: FirebaseRepository,
    private val shopifyRepository: ShopifyAuthRepository
): AuthenticationRepo {

    override suspend fun loginWithEmailAndPassword(
        email: String,
        password: String
    ): Result<Unit> = suspendCoroutine { continuation ->
        firebaseRepository.setFirebaseResponse(object : FirebaseResponse {
            override fun <T> onResponseSuccess(message: T) {
                CoroutineScope(Dispatchers.IO).launch {
                    val shopifyResult = shopifyRepository.login(email, password)
                    if (shopifyResult.isSuccess) {
                        continuation.resume(Result.success(Unit))
                    } else {
                        continuation.resume(Result.failure(shopifyResult.exceptionOrNull() ?: Exception("Shopify login failed")))
                    }
                }
            }

            override fun <T> onResponseFailure(message: T) {
                continuation.resume(Result.failure(Exception(message.toString())))
            }
        })

        firebaseRepository.login(email, password)
    }


    override suspend fun registerWithEmailAndPassword(
        name: String,
        phone: String,
        email: String,
        password: String
    ): Result<Unit> = suspendCoroutine { continuation ->
        firebaseRepository.setFirebaseResponse(object : FirebaseResponse {
            override fun <T> onResponseSuccess(message: T) {
                continuation.resume(Result.success(Unit))
                CoroutineScope(Dispatchers.IO).launch {
                    val shopifyResult = shopifyRepository.register(name, email, password, phone)
                    if (shopifyResult.isSuccess) {
                        continuation.resume(Result.success(Unit))
                    } else {
                        continuation.resume(Result.failure(shopifyResult.exceptionOrNull() ?: Exception("Shopify registration failed")))
                    }
                }
            }

            override fun <T> onResponseFailure(message: T) {
                continuation.resume(Result.failure(Exception(message.toString())))
            }
        })
        firebaseRepository.signup(name, phone, email, password)
    }

    override suspend fun logInWithGoogle(context: Context, launcher: ActivityResultLauncher<Intent>): Result<Unit> {
        val signInIntent = firebaseRepository.getGoogleSignInIntent(context)
        return if (signInIntent != null) {
            launcher.launch(signInIntent)
            Result.success(Unit)
        } else {
            Result.failure(Exception("Missing dependencies"))
        }
    }

    override fun getGoogleSignInIntent(context: Context): Intent? {
        return firebaseRepository.getGoogleSignInIntent(context)
    }

    override suspend fun sendResetPasswordEmail(email: String): Result<String> {
        return suspendCoroutine { continuation ->
            firebaseRepository.setFirebaseResponse(object : FirebaseResponse {
                override fun <T> onResponseSuccess(message: T) {
                    continuation.resume(Result.success("Password reset email sent."))
                }

                override fun <T> onResponseFailure(message: T) {
                    continuation.resume(Result.failure(Exception(message.toString())))
                }
            })
            firebaseRepository.sendPasswordResetEmail(email)
        }
    }

    override fun saveGoogleUserToFireStore(context: Context) {
        firebaseRepository.saveGoogleUserToFireStore(context)
    }

    override suspend fun logout(): Result<Unit> {
        firebaseRepository.logout()
        var result: Result<Unit> = Result.failure(Exception("Login failed"))

        firebaseRepository.setFirebaseResponse(object : FirebaseResponse {
            override fun <T> onResponseSuccess(message: T) {
                result = Result.success(Unit)
            }

            override fun <T> onResponseFailure(message: T) {
                result = Result.failure(Exception(message.toString()))
            }

        })
        return result
    }
}