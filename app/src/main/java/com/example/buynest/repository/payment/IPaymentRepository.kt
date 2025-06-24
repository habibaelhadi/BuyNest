package com.example.buynest.repository.payment

import com.google.gson.JsonObject
import retrofit2.Response

interface IPaymentRepository {
    suspend fun createPaymentIntent(
        amount: Int,
        currency: String,
        paymentMethod: String = "card"
    ): Response<JsonObject>
}
