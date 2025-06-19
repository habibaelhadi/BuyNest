package com.example.buynest.repository.authenticationrepo.firebase

interface FirebaseResponse {
    fun onResponseSuccess(message: String?)
    fun onResponseFailure(message: String?)
}