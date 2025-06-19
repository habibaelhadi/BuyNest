package com.example.buynest.repository.payment

import com.example.buynest.model.remote.rest.IRemoteDataSource
import com.google.gson.JsonObject
import retrofit2.Response

class PaymentRepository(private val remoteDataSource: IRemoteDataSource) {

    suspend fun createPaymentIntent(
        amount: Int,
        currency: String,
        paymentMethod: String = "card"
    ): Response<JsonObject> {
        return remoteDataSource.createPaymentIntent(amount, currency)
    }
}
