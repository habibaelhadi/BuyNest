package com.example.buynest.utils.validators

import com.example.buynest.utils.strategies.AuthenticationStrategy
import com.example.buynest.utils.strategies.GoogleAuthenticationStrategy

class GoogleValidator : ValidationHandler() {
    public override fun validate(strategy: AuthenticationStrategy): String? {
        val actualStrategy = strategy as GoogleAuthenticationStrategy
        return when {
            actualStrategy.context == null -> "Context is required for Google sign-in"
            actualStrategy.launcher == null -> "Launcher is required for Google sign-in"
            else -> null
        }
    }

    override fun handleStrategy(strategy: AuthenticationStrategy): Boolean {
        return strategy is GoogleAuthenticationStrategy
    }
}