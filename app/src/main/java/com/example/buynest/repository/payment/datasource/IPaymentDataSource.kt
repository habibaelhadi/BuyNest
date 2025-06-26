package com.example.buynest.repository.payment.datasource

import com.google.gson.JsonObject
import retrofit2.Response

interface IPaymentDataSource {
    suspend fun createPaymentIntent(
        amount: Int,
        currency: String,
        paymentMethod: String = "card"
    ): Response<JsonObject>
}