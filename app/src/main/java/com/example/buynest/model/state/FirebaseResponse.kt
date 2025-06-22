package com.example.buynest.model.state

interface FirebaseResponse {
    fun <T> onResponseSuccess(message: T)
    fun <T> onResponseFailure(message: T)
}