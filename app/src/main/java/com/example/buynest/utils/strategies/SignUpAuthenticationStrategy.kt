package com.example.buynest.utils.strategies

import com.example.buynest.repos.authenticationrepo.AuthenticationRepo

class SignUpAuthenticationStrategy(
    private val name: String,
    private val phone: String,
    private val email: String,
    private val password: String
): AuthenticationStrategy {

    override suspend fun authenticate(repo: AuthenticationRepo): Result<Unit> {
        return repo.registerWithEmailAndPassword(name, phone, email, password)
    }
}