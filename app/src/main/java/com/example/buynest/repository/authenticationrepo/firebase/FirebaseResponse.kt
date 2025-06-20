package com.example.buynest.repository.authenticationrepo.firebase

interface FirebaseResponse {
    fun <T> onResponseSuccess(message: T)
    fun <T> onResponseFailure(message: T)
}