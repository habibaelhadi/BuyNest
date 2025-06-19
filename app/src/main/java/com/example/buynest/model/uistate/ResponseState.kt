package com.example.buynest.model.uistate

sealed class ResponseState {
    data object Loading : ResponseState()
    data class Success<T>(val data: T) : ResponseState()
    data class Error(val message: String) : ResponseState()
}