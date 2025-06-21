package com.example.buynest.utils.validators

import android.util.Log
import com.example.buynest.utils.strategies.AuthenticationStrategy
import com.example.buynest.utils.strategies.SignUpAuthenticationStrategy

class SignUpValidator: ValidationHandler() {
    override fun handleStrategy(strategy: AuthenticationStrategy): Boolean {
        Log.i("TAG", "handleStrategy: ${strategy.javaClass.simpleName}")
        return strategy is SignUpAuthenticationStrategy
    }

    override fun validate(strategy: AuthenticationStrategy): String? {
        val actualStrategy = strategy as SignUpAuthenticationStrategy
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")
        val phoneRegex = Regex("^01[0-2,5]{1}[0-9]{8}$")
        Log.i("TAG", "validate: *************${actualStrategy.name}")
        return when {
            actualStrategy.name.isEmpty() && actualStrategy.phone.isEmpty() && actualStrategy.email.isEmpty() && actualStrategy.password.isEmpty()
                 -> "Fill all fields"
            actualStrategy.name.isEmpty() -> "Name is required"
            actualStrategy.phone.isEmpty() -> "Phone is required"
            actualStrategy.email.isEmpty() -> "Email is required"
            !actualStrategy.phone.matches(phoneRegex) -> "Phone number is invalid"
            !actualStrategy.email.matches(emailRegex) -> "Email format is invalid"
            actualStrategy.password.isEmpty() -> "Password is required"
            actualStrategy.password.length < 6 -> "Password must be at least 6 characters"
            else -> null
        }
    }
}