package com.example.buynest.model.repository

import com.google.firebase.auth.FirebaseAuth

object FirebaseAuthObject {
    fun getAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }
}