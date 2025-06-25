package com.example.buynest.repository.payment.datasource

import com.example.buynest.model.data.remote.rest.StripeAPI
import com.google.gson.JsonObject
import retrofit2.Response

class PaymentDataSourceImpl(
    private val stripeAPI: StripeAPI
) : IPaymentDataSource {

    override suspend fun createPaymentIntent(
        amount: Int,
        currency: String,
        paymentMethod: String
    ): Response<JsonObject> {
        return stripeAPI.createPaymentIntent(amount, currency, paymentMethod)
    }



}