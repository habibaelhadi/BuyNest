package com.example.buynest.utils.validators

abstract class ValidationHandler {
    private var next: ValidationHandler? = null

    fun setNext(handler: ValidationHandler): ValidationHandler {
        next = handler
        return handler
    }

    fun handle(): String? {
        val result = validate()
        return result ?: next?.handle()
    }

    protected abstract fun validate(): String?
}