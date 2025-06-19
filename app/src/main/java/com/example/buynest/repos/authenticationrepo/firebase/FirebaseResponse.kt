package com.example.buynest.repos.authenticationrepo.firebase

interface FirebaseResponse {
    fun <T> onResponseSuccess(message: T)
    fun <T> onResponseFailure(message: T)
}