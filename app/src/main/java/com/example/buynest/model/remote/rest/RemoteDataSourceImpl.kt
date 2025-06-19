package com.example.buynest.model.remote.rest

import com.google.gson.JsonObject
import retrofit2.Response

class RemoteDataSourceImpl(
    private val stripeAPI: StripeAPI
) : IRemoteDataSource {

    override suspend fun createPaymentIntent(
        amount: Int,
        currency: String,
        paymentMethod: String
    ): Response<JsonObject> {
        return stripeAPI.createPaymentIntent(amount, currency, paymentMethod)
    }
}
