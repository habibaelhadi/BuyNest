package com.example.buynest.utils.strategies

class GoogleAuthenticationStrategy: AuthenticationStrategy {
    override suspend fun login(): Result<Unit> {
        return Result.success(Unit)
    }
}