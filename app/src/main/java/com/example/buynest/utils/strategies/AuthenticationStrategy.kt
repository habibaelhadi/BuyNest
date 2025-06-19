package com.example.buynest.utils.strategies

import com.example.buynest.repository.authenticationrepo.AuthenticationRepo

interface AuthenticationStrategy {
    suspend fun authenticate(repo: AuthenticationRepo): Result<Unit>
}