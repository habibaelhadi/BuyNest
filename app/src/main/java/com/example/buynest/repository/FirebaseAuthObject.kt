package com.example.buynest.repository

import com.google.firebase.auth.FirebaseAuth

object FirebaseAuthObject {
    fun getAuth(): FirebaseAuth{
        return FirebaseAuth.getInstance()
    }
}