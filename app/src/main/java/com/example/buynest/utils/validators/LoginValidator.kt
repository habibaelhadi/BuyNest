package com.example.buynest.utils.validators

import com.example.buynest.utils.strategies.AuthenticationStrategy
import com.example.buynest.utils.strategies.LoginAuthenticationStrategy

class LoginValidator: ValidationHandler() {
    override fun validate(strategy: AuthenticationStrategy): String? {
        val actualStrategy = strategy as LoginAuthenticationStrategy
        return when {
            actualStrategy.email.isEmpty() && actualStrategy.password.isEmpty() -> "Fill all fields"
            actualStrategy.email.isEmpty() -> "Email is required"
            actualStrategy.password.isEmpty() -> "Password is required"
            actualStrategy.password.length < 6 -> "Password must be at least 6 characters"
            else -> null
        }
    }

    override  fun handleStrategy(strategy: AuthenticationStrategy): Boolean{
        return strategy is LoginAuthenticationStrategy
    }
}