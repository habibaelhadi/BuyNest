package com.example.buynest.repos

import com.google.firebase.auth.FirebaseAuth

object FirebaseAuthObject {
    fun getAuth(): FirebaseAuth{
        return FirebaseAuth.getInstance()
    }
}