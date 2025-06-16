package com.example.buynest.utils.strategies

class LoginAuthenticationStrategy: AuthenticationStrategy {
    override suspend fun login(): Result<Unit> {
        return Result.success(Unit)
    }
}