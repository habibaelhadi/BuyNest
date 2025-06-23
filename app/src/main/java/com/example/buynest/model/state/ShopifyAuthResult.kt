package com.example.buynest.model.state

sealed class ShopifyAuthResult {
    data class Success(val message: String, val token: String? = null) : ShopifyAuthResult()
    data class Error(val message: String) : ShopifyAuthResult()
}