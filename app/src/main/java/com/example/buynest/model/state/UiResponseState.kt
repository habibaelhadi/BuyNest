package com.example.buynest.model.state

sealed class UiResponseState {
    data object Loading : UiResponseState()
    data class Success<T>(val data: T) : UiResponseState()
    data class Error(val message: String) : UiResponseState()
}