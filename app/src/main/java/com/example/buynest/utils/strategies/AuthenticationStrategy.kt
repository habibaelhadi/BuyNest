package com.example.buynest.utils.strategies

interface AuthenticationStrategy {
    suspend fun login(): Result<Unit>
}