package com.example.buynest.utils.validators

import com.example.buynest.utils.strategies.AuthenticationStrategy

abstract class ValidationHandler {
    private var next: ValidationHandler? = null

    fun setNext(handler: ValidationHandler): ValidationHandler {
        next = handler
        return handler
    }

    fun handle(strategy: AuthenticationStrategy): String? {
        return if (handleStrategy(strategy)) {
            validate(strategy)
        } else {
            next?.handle(strategy)
        }
    }

    protected abstract fun handleStrategy(strategy: AuthenticationStrategy): Boolean

    protected abstract fun validate(strategy: AuthenticationStrategy): String?
}