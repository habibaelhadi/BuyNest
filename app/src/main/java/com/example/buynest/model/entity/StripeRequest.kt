package com.example.buynest.model.entity

data class StripePaymentIntentRequest(
    val amount: Int,
    val currency: String,

)
