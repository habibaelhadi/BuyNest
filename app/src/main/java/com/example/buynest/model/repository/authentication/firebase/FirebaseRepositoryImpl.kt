package com.example.buynest.model.repository.authentication.firebase

import android.content.Context
import android.content.Intent
import com.example.buynest.model.state.FirebaseResponse
import com.example.buynest.model.repository.authentication.firebase.datasource.IFirebaseDataSource

class FirebaseRepositoryImpl(private val firebaseDataSource: IFirebaseDataSource) : FirebaseRepository {
    override fun setFirebaseResponse(firebaseResponse: FirebaseResponse?) {
        firebaseDataSource.setFirebaseResponse(firebaseResponse)
    }

    override fun signup(name: String, phone: String, email: String, password: String) {
        firebaseDataSource.signup(name, phone, email, password)
    }

    override fun login(email: String, password: String) {
        firebaseDataSource.login(email, password)
    }

    override fun logout() {
       firebaseDataSource.logout()
    }

    override fun getGoogleSignInIntent(context: Context): Intent? {
        return firebaseDataSource.getGoogleSignInIntent(context)
    }

    override fun sendPasswordResetEmail(email: String) {
        firebaseDataSource.sendPasswordResetEmail(email)
    }

    override fun saveGoogleUserToFireStore(context: Context) {
        firebaseDataSource.saveGoogleUserToFireStore(context)
    }

    override fun saveShopifyTokenToFireStore(
        customerToken: String,
        customerId: String,
        cartId: String,
        checkOutKey: String
    ) {
        firebaseDataSource.saveShopifyTokenToFireStore(customerToken, customerId, cartId, checkOutKey)
    }
    override suspend fun getCartId(): String {
        return firebaseDataSource.getCartId()
    }
}