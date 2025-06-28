package com.example.buynest.utils.strategies

import com.example.buynest.model.repository.authentication.AuthenticationRepo

interface AuthenticationStrategy {
    suspend fun authenticate(repo: AuthenticationRepo): Result<Unit>
}