package com.example.buynest.repos.authenticationrepo.firebase

interface FirebaseResponse {
    fun onResponseSuccess(message: String?)
    fun onResponseFailure(message: String?)
}