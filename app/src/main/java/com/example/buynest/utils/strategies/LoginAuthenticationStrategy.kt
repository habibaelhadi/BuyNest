package com.example.buynest.utils.strategies

import com.example.buynest.model.repository.authentication.AuthenticationRepo

class LoginAuthenticationStrategy(
     val email: String,
     val password: String
): AuthenticationStrategy {

    override suspend fun authenticate(repo: AuthenticationRepo): Result<Unit> {
        return repo.loginWithEmailAndPassword(email, password)
    }
}