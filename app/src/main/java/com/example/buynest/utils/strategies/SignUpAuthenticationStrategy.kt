package com.example.buynest.utils.strategies

import com.example.buynest.repos.authenticationrepo.AuthenticationRepo

class SignUpAuthenticationStrategy(
     val name: String,
     val phone: String,
     val email: String,
     val password: String
): AuthenticationStrategy {

    override suspend fun authenticate(repo: AuthenticationRepo): Result<Unit> {
        return repo.registerWithEmailAndPassword(name, phone, email, password)
    }
}