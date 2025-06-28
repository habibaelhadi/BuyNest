package com.example.buynest.model.repository.authentication.firebase

import android.content.Context
import android.content.Intent
import com.example.buynest.model.state.FirebaseResponse

interface FirebaseRepository {
    fun setFirebaseResponse(firebaseResponse: FirebaseResponse?)
    fun signup(name: String, phone: String, email: String, password: String)
    fun login(email: String, password: String)
    fun logout()
    fun getGoogleSignInIntent(context: Context): Intent?
    fun sendPasswordResetEmail(email: String)
    fun saveGoogleUserToFireStore(context: Context)
    fun saveShopifyTokenToFireStore(
        customerToken: String,
        customerId: String,
        cartId: String,
        checkOutKey: String
    )
    suspend fun getCartId(): String
}