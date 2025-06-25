package com.example.buynest.repository.authentication

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import com.example.buynest.model.state.FirebaseResponse
import com.example.buynest.repository.authentication.firebase.FirebaseRepository
import com.example.buynest.repository.authentication.shopify.ShopifyAuthRepository
import com.example.buynest.utils.AppConstants.KEY_CART_ID
import com.example.buynest.utils.AppConstants.KEY_CHECKOUT_URL
import com.example.buynest.utils.AppConstants.KEY_CUSTOMER_ID
import com.example.buynest.utils.AppConstants.KEY_CUSTOMER_TOKEN
import com.example.buynest.utils.SecureSharedPrefHelper
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
                    val cartId = firebaseRepository.getCartId()
                    Log.i("TAG", "AuthRepo -- CartId: $cartId")
                    val shopifyResult = shopifyRepository.login(email, password,cartId)
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
        var resumed = false

        firebaseRepository.setFirebaseResponse(object : FirebaseResponse {
            override fun <T> onResponseSuccess(message: T) {
                CoroutineScope(Dispatchers.IO).launch {
                    val shopifyResult = shopifyRepository.register(name, email, password, phone)
                    if (!resumed) {
                        resumed = true
                        if (shopifyResult.isSuccess) {
                            val customerId = SecureSharedPrefHelper.getString(KEY_CUSTOMER_ID,"")!!
                            val customerToken = SecureSharedPrefHelper.getString(KEY_CUSTOMER_TOKEN,"")!!
                            val cartId = SecureSharedPrefHelper.getString(KEY_CART_ID,"")!!
                            val checkOutKey = SecureSharedPrefHelper.getString(KEY_CHECKOUT_URL,"")!!
                            firebaseRepository.saveShopifyTokenToFireStore(customerToken, customerId, cartId, checkOutKey)
                            continuation.resume(Result.success(Unit))
                        } else {
                            continuation.resume(Result.failure(shopifyResult.exceptionOrNull() ?: Exception("Shopify registration failed")))
                        }
                    }
                }
            }

            override fun <T> onResponseFailure(message: T) {
                if (!resumed) {
                    resumed = true
                    continuation.resume(Result.failure(Exception(message.toString())))
                }
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

    override suspend fun logout(): Result<Unit> = suspendCoroutine { continuation ->
        firebaseRepository.setFirebaseResponse(object : FirebaseResponse {
            override fun <T> onResponseSuccess(message: T) {
                continuation.resume(Result.success(Unit))
            }

            override fun <T> onResponseFailure(message: T) {
                continuation.resume(Result.failure(Exception(message.toString())))
            }
        })

        firebaseRepository.logout()
    }

}